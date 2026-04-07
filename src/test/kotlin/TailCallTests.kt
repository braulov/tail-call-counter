import kotlin.test.Test
import kotlin.test.assertEquals

class TailCallTests {

    @Test
    fun givenExample() {
        val input = """
            (define (f x)
              (if x
                  (f 1 (f 4))
                  (if x
                      (f 1)
                      4)))
        """.trimIndent()

        assertEquals(2, countProperTailRecursiveCalls(input))
    }

    @Test
    fun simpleTailSelfCall() {
        val input = """
            (define (f x)
              (f x))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallInsideArgumentIsNotTail() {
        val input = """
            (define (f x)
              (g (f x)))
        """.trimIndent()

        assertEquals(0, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallInIfConditionIsNotTail() {
        val input = """
            (define (f x)
              (if (f x)
                  1
                  2))
        """.trimIndent()

        assertEquals(0, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallsInBothBranchesCount() {
        val input = """
            (define (f x)
              (if x
                  (f 1)
                  (f 2)))
        """.trimIndent()

        assertEquals(2, countProperTailRecursiveCalls(input))
    }

    @Test
    fun nestedTailAndNonTailSelfCalls() {
        val input = """
            (define (f x)
              (if x
                  (g (f 1))
                  (f (g 2))))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun noSelfCalls() {
        val input = """
            (define (f x)
              (if x
                  (g 1)
                  (h 2)))
        """.trimIndent()

        assertEquals(0, countProperTailRecursiveCalls(input))
    }

    @Test
    fun deeplyNestedTailThroughIfs() {
        val input = """
            (define (f x)
              (if x
                  (if x
                      (if x
                          (f 1)
                          0)
                      0)
                  0))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun deepNestingInsideArgumentsDoesNotCount() {
        val input = """
            (define (f x)
              (g 1
                 (h 2
                    (if x
                        (f 1)
                        (f 2)))))
        """.trimIndent()

        assertEquals(0, countProperTailRecursiveCalls(input))
    }

    @Test
    fun oneTailAndSeveralNonTailNestedSelfCalls() {
        val input = """
            (define (f x)
              (if x
                  (f (f (f 1)))
                  (g (if x
                         (f 2)
                         3))))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun multipleTailCallsAcrossNestedBranches() {
        val input = """
            (define (f x)
              (if x
                  (if x
                      (f 1)
                      (f 2))
                  (if x
                      (g (f 3))
                      (if x
                          (f 4)
                          5))))
        """.trimIndent()

        assertEquals(3, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallInConditionOfNestedIfDoesNotCount() {
        val input = """
            (define (f x)
              (if x
                  (if (f 1)
                      (f 2)
                      3)
                  4))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun classicFactorialIsNotTailRecursive() {
        val input = """
            (define (factorial n)
              (if (= n 0)
                  1
                  (* n
                     (factorial (- n 1)))))
        """.trimIndent()

        assertEquals(0, countProperTailRecursiveCalls(input))
    }

    @Test
    fun tailRecursiveFactorialCountsAsOne() {
        val input = """
            (define (factorial n acc)
              (if (= n 0)
                  acc
                  (factorial (- n 1) (* acc n))))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallHiddenInsideTailCallArgumentDoesNotCountTwice() {
        val input = """
            (define (f x)
              (if x
                  (f (f 1))
                  0))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun selfCallInConditionAndTailBranch() {
        val input = """
            (define (f x)
              (if (f 0)
                  (f 1)
                  (g (f 2))))
        """.trimIndent()

        assertEquals(1, countProperTailRecursiveCalls(input))
    }

    @Test
    fun nestedIfKeepsTailPositionInBothBranches() {
        val input = """
            (define (f x)
              (if x
                  (if x
                      (f 1)
                      (g 2))
                  (if x
                      3
                      (f 4))))
        """.trimIndent()

        assertEquals(2, countProperTailRecursiveCalls(input))
    }
}