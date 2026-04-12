package com.CSI3370.recipedock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
class ValidatorsTest {

    @Test
    fun validUsername_returnsTrue() {
        assertTrue(Validators.usernameOk("chef123"))
    }

    @Test
    fun shortUsername_returnsFalse() {
        assertFalse(Validators.usernameOk("ab"))
    }

    @Test
    fun usernameWithSpace_returnsFalse() {
        assertFalse(Validators.usernameOk("chef 123"))
    }

    @Test
    fun validPassword_returnsTrue() {
        assertTrue(Validators.passwordOk("abc12345"))
    }

    @Test
    fun passwordWithoutNumber_returnsFalse() {
        assertFalse(Validators.passwordOk("abcdefgh"))
    }

    @Test
    fun passwordWithoutLetter_returnsFalse() {
        assertFalse(Validators.passwordOk("12345678"))
    }
}