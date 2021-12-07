fun main() {

    fun median(crabs: List<Int>) = crabs.sorted()[crabs.size / 2 + crabs.size % 2]

    fun progressionSum(n: Int) =
        1.rangeTo(n)
            .sum()

    fun findCost(crab: Int, pos: Int) =
        if (crab - pos < 0) {
            -(crab - pos)
        } else {
            crab - pos
        }

    fun findCostPart2(crab: Int, pos: Int) =
        if (crab - pos < 0) {
            progressionSum(-(crab - pos))
        } else {
            progressionSum(crab - pos)
        }

    fun part1(input: String): Int {
        val crabs = input
            .split(",")
            .map { it.toInt() }

        val pos = median(crabs)

        val leastCost = crabs
            .sumOf { findCost(it, pos) }

        return leastCost
    }

    fun part2(input: String): Int {
        val crabs = input
            .split(",")
            .map { it.toInt() }

        val pos = crabs
            .average()
            .toInt()

        val leastCost = crabs
            .sumOf { findCostPart2(it, pos) }

        return leastCost
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07")
    check(part1(testInput) == 336131)
    check(part2(testInput) == 92676646)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}