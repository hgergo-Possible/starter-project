package com.possible.demo.data.shared

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Reads content of the given [fileName] resource file into a String
 */
fun Any.readJsonResourceFileToString(fileName: String): String {
    val path = this::class.java.classLoader!!.getResource(fileName).toURI().path
    return Files.lines(Paths.get(path)).collect(Collectors.joining())
}