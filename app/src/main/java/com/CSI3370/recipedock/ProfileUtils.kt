package com.CSI3370.recipedock
object ProfileUtils {

    fun displayNameOk(name: String): Boolean {
        return name.trim().length in 1..14
    }

    fun cleanBio(bio: String): String {
        return bio.trim()
    }

    fun canPinMore(currentPinnedCount: Int): Boolean {
        return currentPinnedCount < 3
    }
}