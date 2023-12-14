

package org.matrix.android.sdk.api.util

import java.math.BigInteger

object StringOrderUtils {

    val DEFAULT_ALPHABET = buildString {
        for (i in 0x20..0x7E) {
            append(Char(i))
        }
    }.toCharArray()

    

    fun average(left: String, right: String, alphabet: CharArray = DEFAULT_ALPHABET): String? {
        return midPoints(left, right, 1, alphabet)?.firstOrNull()
    }

    fun midPoints(left: String, right: String, count: Int, alphabet: CharArray = DEFAULT_ALPHABET): List<String>? {
        if (left == right) return null 
        if (left > right) return midPoints(right, left, count, alphabet)
        val size = maxOf(left.length, right.length)
        val leftPadded = pad(left, size, alphabet.first())
        val rightPadded = pad(right, size, alphabet.first())
        val b1 = stringToBase(leftPadded, alphabet)
        val b2 = stringToBase(rightPadded, alphabet)
        val step = (b2.minus(b1)).div(BigInteger("${count + 1}"))
        val orders = mutableListOf<String>()
        var previous = left
        for (i in 0 until count) {
            val newOrder = baseToString(b1.add(step.multiply(BigInteger("${i + 1}"))), alphabet)
            orders.add(newOrder)
            
            if (previous >= newOrder) return null
            previous = newOrder
        }
        return orders.takeIf { orders.last() < right }
    }

    private fun pad(string: String, size: Int, padding: Char): String {
        val raw = string.toCharArray()
        return CharArray(size).also {
            for (index in it.indices) {
                if (index < raw.size) {
                    it[index] = raw[index]
                } else {
                    it[index] = padding
                }
            }
        }.joinToString("")
    }

    fun baseToString(x: BigInteger, alphabet: CharArray): String {
        val len = alphabet.size.toBigInteger()
        if (x < len) {
            return alphabet[x.toInt()].toString()
        } else {
            return baseToString(x.div(len), alphabet) + alphabet[x.rem(len).toInt()].toString()
        }
    }

    fun stringToBase(x: String, alphabet: CharArray): BigInteger {
        if (x.isEmpty()) throw IllegalArgumentException()
        val len = alphabet.size.toBigInteger()
        var result = BigInteger("0")
        x.reversed().forEachIndexed { index, c ->
            result += (alphabet.indexOf(c).toBigInteger() * len.pow(index))
        }
        return result
    }
}
