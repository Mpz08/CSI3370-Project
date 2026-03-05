package com.CSI3370.recipedock

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    data class Result(val ok: Boolean, val msg: String)

    fun signUp(usernameRaw: String, emailRaw: String, password: String, cb: (Result) -> Unit) {
        val username = usernameRaw.trim()
        val email = emailRaw.trim()

        if (!Validators.usernameOk(username)) {
            cb(Result(false, "Username must be 3–16 chars and have no spaces."))
            return
        }
        if (!Validators.emailOk(email)) {
            cb(Result(false, "Enter a valid email."))
            return
        }
        if (!Validators.passwordOk(password)) {
            cb(Result(false, "Password must be 8–16 chars and include 1 letter + 1 number."))
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { res ->
                val uid = res.user?.uid
                if (uid == null) {
                    cb(Result(false, "Signup failed (no uid)."))
                    return@addOnSuccessListener
                }

                // Lock username so it’s unique
                val usernameKey = username.lowercase()
                val usernameDoc = db.collection("usernames").document(usernameKey)
                val userDoc = db.collection("users").document(uid)

                db.runTransaction { tx ->
                    if (tx.get(usernameDoc).exists()) {
                        throw IllegalStateException("Username already taken.")
                    }

                    tx.set(usernameDoc, mapOf(
                        "uid" to uid,
                        "createdAt" to FieldValue.serverTimestamp()
                    ))

                    tx.set(userDoc, mapOf(
                        "uid" to uid,
                        "username" to username,
                        "usernameKey" to usernameKey,
                        "email" to email,
                        "createdAt" to FieldValue.serverTimestamp()
                    ))
                }.addOnSuccessListener {
                    cb(Result(true, "Account created!"))
                }.addOnFailureListener { err ->
                    // remove the auth user if username storage failed
                    auth.currentUser?.delete()
                    cb(Result(false, err.message ?: "Failed to store user."))
                }
            }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Signup failed."))
            }
    }
}