package edu.kamshanski.sortgarbagerussia.utils.string

import kotlin.random.Random

class StringRandom {
    val rnd = Random.Default

    fun nextString(size: Int = 10,
                   latinLowercase: Boolean = true,
                   latinUppercase: Boolean = true,
                   digits: Boolean = true
    ) : String {
        require(size != null && size > 0)

        val lowerLimit = arrayOf(
                if (latinLowercase) 97 else Int.MAX_VALUE,
                if (latinUppercase) 65 else Int.MAX_VALUE,
                if (digits) 48 else Int.MAX_VALUE,
        ).minOrNull()!!

        val upperLimit = arrayOf(
                if (latinLowercase) 122 else Int.MIN_VALUE,
                if (latinUppercase) 90 else Int.MIN_VALUE,
                if (digits) 57 else Int.MIN_VALUE,
        )

        val bytes = ByteArray(size) { _ ->
            do {
                val i = rnd.nextInt()
                val accept = (
                        (latinLowercase && (i in 97..122) ) ||
                                (latinUppercase && (i in 65..90) ) ||
                                (digits && (i in 48..57) )
                        )
                if (accept)
                    return@ByteArray i.toByte()
            } while (true)
            5
        }
        return String(bytes, Charsets.UTF_8)
    }

}