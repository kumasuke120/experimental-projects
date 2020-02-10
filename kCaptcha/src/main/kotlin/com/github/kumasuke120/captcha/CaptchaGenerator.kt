package com.github.kumasuke120.captcha

import java.awt.Color
import java.awt.Font
import java.awt.FontFormatException
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.imageio.ImageIO

class CaptchaGenerator private constructor(
        internal val codeLength: Int,
        internal val imageType: ImageType,
        internal val imageWidth: Int,
        internal val imageHeight: Int,
        internal val maskLineNumber: Int
) {
    companion object {
        private const val AVAILABLE_FONTS_COUNT = 12
        private const val AVAILABLE_FONT_NAME_TEMPLATE = "/captcha_fonts/captcha_font_%d.ttf"

        private val availableCharacters = charArrayOf(
                '2', '3', '4', '6', '7',
                'A', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        )
        private val defaultGeneratorInstance: CaptchaGenerator by lazy {
            builder().build()
        }

        private val random: Random = SecureRandom()
        private val fontBytesCache: ConcurrentMap<Int, ByteArray> = ConcurrentHashMap(AVAILABLE_FONTS_COUNT)

        @JvmStatic fun builder(): Builder {
            return Builder()
        }

        @JvmStatic fun defaultGenerator(): CaptchaGenerator {
            return defaultGeneratorInstance
        }
    }

    class Builder {
        private var codeLength: Int = 4
        private var imageType: ImageType = ImageType.PNG
        private var imageWidth: Int = 100
        private var imageHeight: Int = 34
        private var maskLineNumber: Int = 50

        fun codeLength(codeLength: Int): Builder {
            this.codeLength = codeLength
            return this
        }

        @JvmOverloads fun image(type: ImageType? = null, width: Int? = null, height: Int? = null): Builder {
            if (type != null) this.imageType = type
            if (width != null) this.imageWidth = width
            if (height != null) this.imageHeight = height
            return this
        }

        fun maskLineNumber(maskLineNumber: Int): Builder {
            this.maskLineNumber = maskLineNumber
            return this
        }

        fun build(): CaptchaGenerator {
            return CaptchaGenerator(codeLength, imageType, imageWidth, imageHeight, maskLineNumber)
        }
    }

    fun nextCaptcha(): Captcha {
        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
        val graphics: Graphics2D = image.createGraphics()

        drawBasicStructure(graphics)
        drawMaskLines(graphics)

        val code: String = nextRandomCode()
        drawCode(graphics, code)

        val imageBytes: ByteArray = generateImageBytes(image)
        graphics.dispose()
        return Captcha(code, imageBytes, imageType)
    }

    private fun drawBasicStructure(graphics: Graphics2D) {
        graphics.color = nextRandomBackgroundColor()
        graphics.fillRect(0, 0, imageWidth, imageHeight)

        graphics.font = nextRandomFont()
    }

    private fun drawMaskLines(graphics: Graphics2D) {
        for (i in 1..maskLineNumber) {
            val startX = random.nextInt(imageWidth)
            val startY = random.nextInt(imageHeight)
            val endX = startX + random.nextInt(imageWidth / 8)
            val endY = startY + random.nextInt(imageHeight / 8)

            graphics.color = nextRandomForegroundColor()
            graphics.drawLine(startX, startY, endX, endY)
        }
    }

    private fun nextRandomCode(): String {
        val code = StringBuilder()

        for (i in 1..codeLength) {
            val index = random.nextInt(availableCharacters.size)
            val nextChar = availableCharacters[index]
            code.append(nextChar)
        }

        return code.toString()
    }

    private fun drawCode(graphics: Graphics2D, code: String) {
        val codeCharWidth = imageWidth / (codeLength * 2 + 2)
        val fontMetrics = graphics.fontMetrics

        var posX = codeCharWidth
        code.forEach {
            val currentChar: String = it.toString()
            val posY = random.nextInt(imageHeight - fontMetrics.ascent) + fontMetrics.ascent

            graphics.color = nextRandomForegroundColor()
            graphics.drawString(currentChar, posX, posY)

            posX += 2 * codeCharWidth
        }
    }

    private fun generateImageBytes(image: BufferedImage): ByteArray {
        val output = ByteArrayOutputStream()

        output.use {
            try {
                ImageIO.write(image, imageType.formatName, output)
            } catch (e: IOException) {
                throw CaptchaGenerateException("Error encountered when generating image bytes", e)
            }
            return output.toByteArray()
        }
    }

    private fun nextRandomBackgroundColor(): Color {
        val r = random.nextInt(36) + 220
        val g = random.nextInt(36) + 220
        val b = random.nextInt(36) + 220
        return Color(r, g, b)
    }

    private fun nextRandomFont(): Font {
        val fontIndex = nextRandomFontIndex()

        if (!fontBytesCache.containsKey(fontIndex)) {
            val fontName = String.format(AVAILABLE_FONT_NAME_TEMPLATE, fontIndex)
            val fontBytes = getClasspathResourceAsByteArray(fontName)
            fontBytesCache.putIfAbsent(fontIndex, fontBytes)
        }

        return createFontFromCache(fontIndex)
    }

    private fun nextRandomForegroundColor(): Color {
        val r = random.nextInt(128)
        val g = random.nextInt(128)
        val b = random.nextInt(128)
        return Color(r, g, b)
    }

    private fun nextRandomFontIndex(): Int {
        return random.nextInt(AVAILABLE_FONTS_COUNT) + 1
    }

    private fun getClasspathResourceAsByteArray(filePath: String): ByteArray {
        val input = this.javaClass.getResourceAsStream(filePath)
        input.use {
            try {
                return input.readBytes()
            } catch (e: IOException) {
                throw CaptchaGenerateException("Error encountered when reading font", e)
            }
        }
    }

    private fun createFontFromCache(fontIndex: Int): Font {
        val fontBytes: ByteArray? = fontBytesCache[fontIndex]
        if (fontBytes != null) {
            return createFont(fontBytes)
        } else {
            throw CaptchaGenerateException(AssertionError("Shouldn't happen"))
        }
    }

    private fun createFont(fontBytes: ByteArray): Font {
        val fontStream: InputStream = ByteArrayInputStream(fontBytes)
        val baseFont = tryToCreateFontFromStream(fontStream)
        val fontSize = (imageHeight * 0.8).toFloat()
        return baseFont.deriveFont(Font.PLAIN, fontSize)
    }

    private fun tryToCreateFontFromStream(fontStream: InputStream): Font {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, fontStream)
        } catch (e: FontFormatException) {
            throw CaptchaGenerateException("Unsupported formatName, which shouldn't happen", e)
        } catch (e: IOException) {
            throw CaptchaGenerateException("Error encountered when creating font", e)
        }
    }
}