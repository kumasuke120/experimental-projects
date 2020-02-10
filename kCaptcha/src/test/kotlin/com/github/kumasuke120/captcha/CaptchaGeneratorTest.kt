package com.github.kumasuke120.captcha

import com.github.kumasuke120.test.requireConfirm
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptchaGeneratorTest {
    @Test fun testBuild() {
        val generator: CaptchaGenerator = CaptchaGenerator.builder()
                .codeLength(8)
                .image(ImageType.BMP, width = 240, height = 40)
                .maskLineNumber(80)
                .build()

        assertEquals(8, generator.codeLength)
        assertEquals(240, generator.imageWidth)
        assertEquals(40, generator.imageHeight)
        assertEquals(ImageType.BMP, generator.imageType)
        assertEquals(80, generator.maskLineNumber)
    }

    @Test fun testGenerate() {
        val generator = CaptchaGenerator.defaultGenerator()
        val captcha = generator.nextCaptcha()

        assertEquals(generator.codeLength, captcha.code.length)
        assertTrue(captcha.imageBytes.isNotEmpty())
        assertEquals(generator.imageType, captcha.type)

        requireConfirm(captcha.imageBytes, "code: ${captcha.code}")
    }

    @Test fun generateOneHundredTimes() {
        val generator = CaptchaGenerator.defaultGenerator()

        val elapsedTime = measureTimeMillis {
            for (i in 1..100) {
                generator.nextCaptcha()
            }
        }

        println("time elapsed: ${elapsedTime}ms, " +
                        "average time elapsed: ${elapsedTime / 100}ms")
    }

    @Test fun testForAllImageTypes() {
        ImageType.values().forEach {
            val generator = CaptchaGenerator.builder()
                    .image(it)
                    .build()
            val captcha = generator.nextCaptcha()

            assertEquals(it, generator.imageType)
            assertEquals(it, captcha.type)

            requireConfirm(captcha.imageBytes, "type: ${captcha.type}, code: ${captcha.code}")
        }
    }

    @Test fun defaultGeneratorShouldBeSingleton() {
        val first = CaptchaGenerator.defaultGenerator()
        val second = CaptchaGenerator.defaultGenerator()

        assertTrue(first === second)
    }
}