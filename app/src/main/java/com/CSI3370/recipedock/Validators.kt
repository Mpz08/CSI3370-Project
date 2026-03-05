package com.CSI3370.recipedock

import android.util.Patterns

object Validators {

    fun usernameOk(username: String): Boolean {
        if (username.length !in 3..16) return false
        if (username.contains(" ")) return false
        return true
    }

    fun emailOk(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordOk(password: String): Boolean {
        if (password.length !in 8..16) return false

        val hasLetter = password.any { it.isLetter() }
        val hasNumber = password.any { it.isDigit() }

        return hasLetter && hasNumber
    }
}