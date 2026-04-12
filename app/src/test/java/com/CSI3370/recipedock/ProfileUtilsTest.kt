package com.CSI3370.recipedock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
class ProfileUtilsTest {

    @Test
    fun validDisplayName_returnsTrue() {
        assertTrue(ProfileUtils.displayNameOk("ChefTony"))
    }

    @Test
    fun tooLongDisplayName_returnsFalse() {
        assertFalse(ProfileUtils.displayNameOk("VeryLongDisplayName"))
    }

    @Test
    fun cleanBio_removesSpaces() {
        assertEquals("Hello world", ProfileUtils.cleanBio("   Hello world   "))
    }

    @Test
    fun canPinMore_whenLessThanThree_returnsTrue() {
        assertTrue(ProfileUtils.canPinMore(2))
    }

    @Test
    fun canPinMore_whenThree_returnsFalse() {
        assertFalse(ProfileUtils.canPinMore(3))
    }
}