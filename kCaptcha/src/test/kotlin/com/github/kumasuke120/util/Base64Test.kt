package com.github.kumasuke120.util

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Base64Test {
    @Test fun testEncode() {
        val data = "test~qwerty~!@#~123"

        val expected = "dGVzdH5xd2VydHl+IUAjfjEyMw=="
        val actual = Base64.encode(data.toByteArray())
        assertEquals(expected, actual)
    }

    @Test fun testDecode() {
        val str = "dGVzdH5xd2VydHl+IUAjfjEyMw=="

        val expected = "test~qwerty~!@#~123".toByteArray()
        val actual = Base64.decode(str)
        assertTrue(Arrays.equals(expected, actual))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDecodeExceptionWhenInvalidLength() {
        Base64.decode("123")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDecodeExceptionWhenInvalidCharacter() {
        Base64.decode("@@@@")
    }
}