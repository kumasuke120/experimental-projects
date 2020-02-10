package com.github.kumasuke120.util

import java.io.ByteArrayOutputStream

object Base64 {
    private val encodingTable: CharArray = charArrayOf(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    )

    private val decodingTable: Map<Char, Int> by lazy {
        val table = HashMap<Char, Int>(encodingTable.size)
        for ((i, c) in encodingTable.withIndex()) {
            table[c] = i
        }
        table.toMap()
    }

    fun encode(data: ByteArray): String {
        val result = StringBuilder()
        var paddingNumber = 0

        for (i in 0 until data.size step 3) {
            var triple = 0

            for (j in 0..2) {
                triple = triple shl 8
                if (i + j < data.size) {
                    val octet = data[i + j].toInt() and 0xFF
                    triple = triple or octet
                } else {
                    paddingNumber += 1
                }
            }

            (3 downTo paddingNumber)
                    .map { (triple shr 6 * it) and 0x3F }
                    .forEach { result.append(encodingTable[it]) }
        }

        for (i in 1..paddingNumber) {
            result.append("=")
        }

        return result.toString()
    }

    fun decode(str: String): ByteArray {
        if (str.length % 4 != 0 || !str.matches("[a-zA-Z0-9+/=]*".toRegex())) {
            throw IllegalArgumentException("invalid Base64 encoded String")
        } else {
            val result = ByteArrayOutputStream()

            for (i in 0 until str.length step 4) {
                var quadruple = 0

                for (j in 0..3) {
                    quadruple = quadruple shl 6
                    val sextet = getSextetFromChar(str[i + j])
                    quadruple = quadruple or (sextet and 0x3F)
                }

                (1..3)
                        .map { (quadruple shr 8 * (3 - it)) and 0xFF }
                        .forEach { result.write(it) }
            }

            result.use {
                val bytes = result.toByteArray()
                return trimByteArray(bytes)
            }
        }
    }

    private fun getSextetFromChar(c: Char): Int {
        return decodingTable[c] ?: 0
    }

    private fun trimByteArray(bytes: ByteArray): ByteArray {
        val newSize = (bytes.size - 1 downTo 0)
                .first { bytes[it] != 0.toByte() } + 1
        return bytes.copyOf(newSize)
    }
}