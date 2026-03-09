package com.novah.mathmind.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing games in Firebase Firestore.
 * Games are shared across all users. Only the creator can delete their game.
 */
class FirebaseGameRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val gamesCollection = firestore.collection("shared_games")
    private val auth = FirebaseAuth.getInstance()

    /** Returns the current user's UID, signing in anonymously if needed. */
    suspend fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) return currentUser.uid
        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: ""
    }

    /** Returns a Flow of all shared games from Firestore, ordered by creation time. */
    fun getAllGames(): Flow<List<CustomGame>> = callbackFlow {
        val listener = gamesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val games = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CustomGame(
                            id = doc.getLong("localId")?.toInt() ?: 0,
                            title = doc.getString("title") ?: "",
                            htmlContent = doc.getString("htmlContent") ?: "",
                            cssContent = doc.getString("cssContent") ?: "",
                            jsContent = doc.getString("jsContent") ?: "",
                            creatorId = doc.getString("creatorId") ?: "",
                            firebaseId = doc.id
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(games)
            }
        awaitClose { listener.remove() }
    }

    /** Adds a new game to Firestore. */
    suspend fun addGame(game: CustomGame): String {
        val userId = getCurrentUserId()
        val data = hashMapOf(
            "title" to game.title,
            "htmlContent" to game.htmlContent,
            "cssContent" to game.cssContent,
            "jsContent" to game.jsContent,
            "creatorId" to userId,
            "createdAt" to Timestamp.now()
        )
        val docRef = gamesCollection.add(data).await()
        return docRef.id
    }

    /** Deletes a game from Firestore. Only succeeds if the current user is the creator. */
    suspend fun deleteGame(firebaseId: String): Boolean {
        val userId = getCurrentUserId()
        val doc = gamesCollection.document(firebaseId).get().await()
        val creatorId = doc.getString("creatorId") ?: ""
        if (creatorId == userId) {
            gamesCollection.document(firebaseId).delete().await()
            return true
        }
        return false
    }

    /** Gets a single game by its Firebase document ID. */
    suspend fun getGameById(firebaseId: String): CustomGame? {
        return try {
            val doc = gamesCollection.document(firebaseId).get().await()
            if (doc.exists()) {
                CustomGame(
                    id = 0,
                    title = doc.getString("title") ?: "",
                    htmlContent = doc.getString("htmlContent") ?: "",
                    cssContent = doc.getString("cssContent") ?: "",
                    jsContent = doc.getString("jsContent") ?: "",
                    creatorId = doc.getString("creatorId") ?: "",
                    firebaseId = doc.id
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
