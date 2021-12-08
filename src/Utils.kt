import java.io.File

/**
 * Reads text from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readText()

/**
 * Reads lines from the given input txt file
 */
fun readInputByLines(name: String) = File("src", "$name.txt").readLines()
