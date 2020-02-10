package com.github.kumasuke120.captcha

import com.github.kumasuke120.util.Base64
import java.util.*

data class Captcha(
        val code: String,
        val imageBytes: ByteArray,
        val type: ImageType
) {
    fun toImageDataUri(): String {
        val encodedData = Base64.encode(imageBytes)
        return "data:${type.mimeType};base64,$encodedData"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Captcha) return false

        return code == other.code &&
                Arrays.equals(imageBytes, other.imageBytes) &&
                type == other.type
    }

    override fun hashCode(): Int {
        var result = 17

        result = result * 31 + code.hashCode()
        result = result * 31 + Arrays.hashCode(imageBytes)
        result = result * 31 + type.hashCode()

        return result
    }
}