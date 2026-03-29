package com.CSI3370.recipedock

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

class ProfileRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    data class ProfileData(
        val displayName: String = "",
        val bio: String = "",
        val profileImageUrl: String = ""
    )

    data class Result(val ok: Boolean, val msg: String)

    fun loadProfile(cb: (ProfileData) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                var displayName = doc.getString("displayName") ?: ""
                val bio = doc.getString("bio") ?: ""
                val profileImageUrl = doc.getString("profileImageUrl") ?: ""

                if (displayName.isBlank()) {
                    displayName = generateRandomDisplayName()
                    db.collection("users").document(uid)
                        .update("displayName", displayName)
                        .addOnSuccessListener {
                            cb(ProfileData(displayName, bio, profileImageUrl))
                        }
                        .addOnFailureListener {
                            cb(ProfileData(displayName, bio, profileImageUrl))
                        }
                } else {
                    cb(ProfileData(displayName, bio, profileImageUrl))
                }
            }
    }

    fun updateDisplayName(newNameRaw: String, cb: (Result) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            cb(Result(false, "User not logged in."))
            return
        }

        val newName = newNameRaw.trim()

        if (newName.length !in 1..14) {
            cb(Result(false, "Display name must be 1 to 14 characters."))
            return
        }

        db.collection("users").document(uid)
            .update("displayName", newName)
            .addOnSuccessListener {
                cb(Result(true, "Display name updated"))
            }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Failed to update display name"))
            }
    }

    fun updateBio(newBioRaw: String, cb: (Result) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            cb(Result(false, "User not logged in."))
            return
        }

        val newBio = newBioRaw.trim()

        db.collection("users").document(uid)
            .update("bio", newBio)
            .addOnSuccessListener {
                cb(Result(true, "Bio updated"))
            }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Failed to update bio"))
            }
    }

    fun uploadProfileImage(imageUri: Uri, cb: (Result, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            cb(Result(false, "User not logged in."), null)
            return
        }

        val ref = storage.reference.child("profile_pictures/$uid.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()

                        db.collection("users").document(uid)
                            .update("profileImageUrl", imageUrl)
                            .addOnSuccessListener {
                                cb(Result(true, "Profile picture updated"), imageUrl)
                            }
                            .addOnFailureListener { err ->
                                cb(Result(false, err.message ?: "Failed to save image URL"), null)
                            }
                    }
                    .addOnFailureListener { err ->
                        cb(Result(false, err.message ?: "Failed to get image URL"), null)
                    }
            }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Image upload failed"), null)
            }
    }

    private fun generateRandomDisplayName(): String {
        val adjectives = listOf(
            "Tasty", "Golden", "Savory", "Crispy", "Zesty",
            "Cozy", "Spicy", "Fresh", "Sizzling", "Sweet"
        )

        val nouns = listOf(
            "Chef", "Plate", "Pan", "Baker", "Cook",
            "Spoon", "Fork", "Recipe", "Dish", "Meal"
        )

        val adjective = adjectives.random()
        val noun = nouns.random()
        val number = Random.nextInt(10, 100)

        return "$adjective$noun$number".take(14)
    }
}
