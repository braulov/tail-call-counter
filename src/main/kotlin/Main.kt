fun main() {
    val input = generateSequence(::readLine).joinToString("\n").trim()

    require(input.isNotEmpty()) {
        "Expected a Scheme function definition on stdin"
    }

    println(countProperTailRecursiveCalls(input))
}