package com.github.kumasuke120.captcha

import com.github.kumasuke120.util.Base64
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CaptchaTest {
    @Test fun testEquals() {
        val a = Captcha("1111", byteArrayOf(0), ImageType.PNG)
        val b = Captcha("1111", byteArrayOf(0), ImageType.PNG)
        val c = Captcha("1111", byteArrayOf(1), ImageType.PNG)
        val d = Captcha("2222", byteArrayOf(1), ImageType.PNG)
        val e = Captcha("2222", byteArrayOf(1), ImageType.BMP)

        assertEquals(a, a)
        assertNotEquals<Captcha?>(a, null)
        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)

        assertTrue(a == a)
        assertTrue(a == b)
        assertFalse(a == c)
        assertFalse(a == d)
        assertFalse(a == e)
    }

    @Test fun testHashCode() {
        val a = Captcha("1111", byteArrayOf(0), ImageType.PNG)
        val b = Captcha("1111", byteArrayOf(0), ImageType.PNG)
        val c = Captcha("1111", byteArrayOf(1), ImageType.PNG)
        val d = Captcha("2222", byteArrayOf(1), ImageType.PNG)
        val e = Captcha("2222", byteArrayOf(1), ImageType.BMP)

        assertEquals(a.hashCode(), a.hashCode())
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a.hashCode(), c.hashCode())
        assertNotEquals(a.hashCode(), d.hashCode())
        assertNotEquals(a.hashCode(), e.hashCode())
    }

    @Test fun testToImageDataUri() {
        val testBase64Image = "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCc" +
                "hPGolfO0o/XBs/fNwfjZ0frl3/zy7////wAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC" +
                "H5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKq" +
                "NKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE" +
                "1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7"
        val testImageBytes = Base64.decode(testBase64Image)
        val captcha = Captcha("XXXX", testImageBytes, ImageType.GIF)
        val expectedDataUri = "data:image/gif;base64,$testBase64Image"

        assertEquals(expectedDataUri, captcha.toImageDataUri())
    }
}