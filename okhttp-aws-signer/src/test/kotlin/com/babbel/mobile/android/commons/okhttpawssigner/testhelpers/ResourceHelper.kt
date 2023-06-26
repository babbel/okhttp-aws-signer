package com.babbel.mobile.android.commons.okhttpawssigner.testhelpers

import okio.buffer
import okio.source

/**
 * Find the string starting with the given start.
 *
 * This method throws if string is not found
 */
fun String.lineStartingWith(start: String) =
    split("\n")
        .find { it.startsWith(start) }
        ?.removePrefix("Authorization:")
        ?.trim()
        ?: throw NoSuchElementException("Cannot find line starting with $start")

object ResourceHelper {
    fun readResource(fileName: String) =
        javaClass.classLoader.getResourceAsStream(fileName)!!.source().buffer()
            .readUtf8()
}

