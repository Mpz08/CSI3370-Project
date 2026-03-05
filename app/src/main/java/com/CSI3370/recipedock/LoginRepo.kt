package com.CSI3370.recipedock

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    data class Result(val ok: Boolean, val msg: String)

    fun loginWithUsername(usernameRaw: String, password: String, cb: (Result) -> Unit) {
        val username = usernameRaw.trim()
        if (!Validators.usernameOk(username)) {
            cb(Result(false, "Username must be 3–16 chars and have no spaces."))
            return
        }
        if (password.isBlank()) {
            cb(Result(false, "Enter your password."))
            return
        }

        val usernameKey = username.lowercase()
        val usernameDoc = db.collection("usernames").document(usernameKey)

        usernameDoc.get()
            .addOnSuccessListener { snap ->
                val uid = snap.getString("uid")
                if (uid.isNullOrBlank()) {
                    cb(Result(false, "No account found for that username."))
                    return@addOnSuccessListener
                }

                db.collection("users").document(uid).get()
                    .addOnSuccessListener { userSnap ->
                        val email = userSnap.getString("email")
                        if (email.isNullOrBlank()) {
                            cb(Result(false, "Account data missing email."))
                            return@addOnSuccessListener
                        }

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { cb(Result(true, "Logged in!")) }
                            .addOnFailureListener { err ->
                                cb(Result(false, err.message ?: "Login failed."))
                            }
                    }
                    .addOnFailureListener { err ->
                        cb(Result(false, err.message ?: "Login failed."))
                    }
            }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Login failed."))
            }
    }

    fun sendReset(emailRaw: String, confirmRaw: String, cb: (Result) -> Unit) {
        val email = emailRaw.trim()
        val confirm = confirmRaw.trim()

        if (!Validators.emailOk(email)) {
            cb(Result(false, "Enter a valid email."))
            return
        }
        if (email != confirm) {
            cb(Result(false, "Emails do not match."))
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { cb(Result(true, "Reset email sent.")) }
            .addOnFailureListener { err ->
                cb(Result(false, err.message ?: "Reset failed."))
            }
    }
}