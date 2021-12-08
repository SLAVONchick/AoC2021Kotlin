class StaticFields {
    companion object {
        @JvmField
        val SimpleNumbers : List<Int> = listOf(2, 4, 3, 7)
    }
}

/*
 * Represents a display with seven segments:
 *
 *      _____
 *     |  1  |
 *    2|     |3
 *     |_____|
 *     |  4  |
 *    5|     |6
 *     |_____|
 *        7
 *
 */
class Display private constructor() {

    private var first : Char = ' '
    private var second : Char = ' '
    private var third : Char = ' '
    private var fourth : Char = ' '
    private var fifth : Char = ' '
    private var sixth : Char = ' '
    private var seventh : Char = ' '
    
    private val zero : List<Char>
        get() = listOf(first, second, third, fifth, sixth, seventh)
    private val one : List<Char>
        get() = listOf(third, sixth)
    private val two : List<Char>
        get() = listOf(first, third, fourth, fifth, seventh)
    private val three : List<Char>
        get() = listOf(first, third, fourth, sixth, seventh)
    private val four : List<Char>
        get() = listOf(second, third, fourth, sixth)
    private val five : List<Char>
        get() = listOf(first, second, fourth, sixth, seventh)
    private val six : List<Char>
        get() = listOf(first, second, fourth, fifth, sixth, seventh)
    private val seven : List<Char>
        get() = listOf(first, third, sixth)
    private val eight : List<Char>
        get() = listOf(first, second, third, fourth, fifth, sixth, seventh)
    private val nine : List<Char>
        get() = listOf(first, second, third, fourth, sixth, seventh)

    companion object {
        fun create(signals: List<String>) : Display {
            val display = Display()

            val one = signals.find { it.length == 2 }!!
            display.third = one[0]
            display.sixth = one[1]

            val four = signals.find { it.length == 4 }!!
            val fourWithoutAlreadyKnownDigits = four
                .filter { it != display.third && it != display.sixth }
            display.second = fourWithoutAlreadyKnownDigits[0]
            display.fourth = fourWithoutAlreadyKnownDigits[1]

            val seven = signals
                .find { it.length == 3 }
                ?.filter { it != display.third && it != display.sixth } !!
            display.first = seven[0]

            val knownSegments = listOf(
                display.first,
                display.second,
                display.third,
                display.fourth,
                display.sixth)

            val eight = signals
                .find { it.length == 7 }
                ?.filter { it !in knownSegments }!!
            display.fifth = eight[0]
            display.seventh = eight[1]

            var success = true

            var iterCount = 1
            while (success) {
                for (signal in signals) {
                    if (display.isNumber(signal) == null) {
                        success = false
                        break
                    }
                }
                if (!success) {
                    if (iterCount > 7)
                        break
                    when (iterCount) {
                        // 001
                        1 -> {
                            display.fourth = fourWithoutAlreadyKnownDigits[0]
                            display.second = fourWithoutAlreadyKnownDigits[1]
                        }
                        // 011
                        2 -> {
                            display.sixth = one[0]
                            display.third = one[1]
                        }
                        // 111
                        3 -> {
                            display.seventh = eight[0]
                            display.fifth = eight[1]
                        }
                        // 101
                        4 -> {
                            display.third = one[0]
                            display.sixth = one[1]
                        }
                        // 100
                        5 -> {
                            display.second = fourWithoutAlreadyKnownDigits[0]
                            display.fourth = fourWithoutAlreadyKnownDigits[1]
                        }
                        // 110
                        6 -> {
                            display.sixth = one[0]
                            display.third = one[1]
                        }
                        // 010
                        7 -> {
                            display.fifth = eight[0]
                            display.seventh = eight[1]
                        }
                    }
                    success = true
                }
                else break

                iterCount++
            }

            if (!success) {
                val notParsed = signals.filter { display.isNumber(it) == null }
                throw Exception("NO SUCCESS IN DETERMINING SIGNALS ${signals.joinToString(", ")} (NOT PARSED VALUES: ${notParsed.joinToString(", ")}) \n$display\n")
            }
            return display
        }

    }

    fun isNumber(input: String) : Int? =
        when (input.length) {
            2 -> if (stringEquals(one, input)) 1 else null
            3 -> if (stringEquals(seven, input)) 7 else null
            4 -> if (stringEquals(four, input)) 4 else null
            7 -> if (stringEquals(eight, input)) 8 else null
            5 -> if (stringEquals(two, input)) {
                2
            } else if (stringEquals(three, input)) {
                3
            } else if (stringEquals(five, input)) {
                5
            } else null
            6 -> if (stringEquals(zero, input)) {
                0
            } else if (stringEquals(six, input)) {
                6
            } else if (stringEquals(nine, input)) {
                9
            } else null
            else -> null
        }

    override fun toString(): String {
        return  "     _____\n" +
                "    |  $first  |\n" +
                "   $second|     |$third\n" +
                "    |_____|\n" +
                "    |  $fourth  |\n" +
                "   $fifth|     |$sixth\n" +
                "    |_____|\n" +
                "       $seventh   \n"
    }
}

fun stringEquals(one: List<Char>, other: String) : Boolean {
    for (c in one) if (!other.contains(c)) {
        return false
    }
    return true
}

fun main() {

    fun countSimpleDigits(output: List<String>) =
        output.filter { when (it.length) {
            in StaticFields.SimpleNumbers -> true
            else -> false
        } }
            .size

    fun part1(input: List<String>): Int {
        val output = input.flatMap { it.split("|")[1].split(" ") }
        return countSimpleDigits(output)
    }

    fun part2(inputs: List<String>): Int {
        var results = 0
        var i = 0
        for (input in inputs) {
            val split = input.split("|")
            val signals = split[0].split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.isNotBlank() }
            try {
                val display = Display.create(signals)
                val outputs = split[1].split(" ")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && it.isNotBlank() }
                var outputNumber = 0
                for (output in outputs) {
                    val num = display.isNumber(output)!!
                    outputNumber = outputNumber * 10 + num
                }
                results += outputNumber
                i++
            }
            catch (e: Exception) {
                throw Exception("${e.message} in $i")
            }
        }
        return results
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputByLines("Day08")
    check(part1(testInput) == 375)
    check(part2(testInput) == 1019355)

    val input = readInputByLines("Day08")
    println(part1(input))
    println(part2(input))
}