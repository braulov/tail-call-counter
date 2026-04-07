sealed interface Expr

data class AtomExpr(val value: String) : Expr

data class IfExpr(
    val condition: Expr,
    val thenBranch: Expr,
    val elseBranch: Expr
) : Expr

data class CallExpr(
    val callee: String,
    val args: List<Expr>
) : Expr

data class Program(
    val functionName: String,
    val params: List<String>,
    val body: Expr
)

fun toExpr(sexpr: SExpr): Expr =
    when (sexpr) {
        is SExpr.Atom -> AtomExpr(sexpr.value)

        is SExpr.ListExpr -> {
            require(sexpr.items.isNotEmpty()) {
                "Empty list is not a valid expression"
            }

            val head = sexpr.items.first()
            require(head is SExpr.Atom) {
                "List head must be an atom"
            }

            if (head.value == "if") {
                require(sexpr.items.size == 4) {
                    "if must have exactly 3 arguments"
                }

                IfExpr(
                    condition = toExpr(sexpr.items[1]),
                    thenBranch = toExpr(sexpr.items[2]),
                    elseBranch = toExpr(sexpr.items[3])
                )
            } else {
                CallExpr(
                    callee = head.value,
                    args = sexpr.items.drop(1).map(::toExpr)
                )
            }
        }
    }

fun toProgram(sexpr: SExpr): Program {
    require(sexpr is SExpr.ListExpr) {
        "Top-level form must be a list"
    }
    require(sexpr.items.size == 3) {
        "Top-level form must be (define (name args...) body)"
    }

    val head = sexpr.items[0]
    require(head is SExpr.Atom && head.value == "define") {
        "Top-level form must start with define"
    }

    val signature = sexpr.items[1]
    require(signature is SExpr.ListExpr) {
        "Function signature must be a list"
    }
    require(signature.items.isNotEmpty()) {
        "Function signature must not be empty"
    }

    val nameExpr = signature.items[0]
    require(nameExpr is SExpr.Atom) {
        "Function name must be an atom"
    }

    val params = signature.items.drop(1).map {
        require(it is SExpr.Atom) {
            "Parameter names must be atoms"
        }
        it.value
    }

    val body = toExpr(sexpr.items[2])

    return Program(
        functionName = nameExpr.value,
        params = params,
        body = body
    )
}

/**
 * Counts syntactic occurrences of proper tail-recursive self-calls.
 *
 * Rules for the restricted Scheme in this task:
 * - in (if cond then else), only then/else can inherit tail position
 * - the condition is never in tail position
 * - call arguments are never in tail position
 */
fun countProperTailSelfCalls(
    expr: Expr,
    selfName: String,
    inTailPosition: Boolean
): Int =
    when (expr) {
        is AtomExpr -> 0

        is IfExpr ->
            countProperTailSelfCalls(expr.condition, selfName, false) +
                    countProperTailSelfCalls(expr.thenBranch, selfName, inTailPosition) +
                    countProperTailSelfCalls(expr.elseBranch, selfName, inTailPosition)

        is CallExpr -> {
            val selfCallHere = if (inTailPosition && expr.callee == selfName) 1 else 0

            selfCallHere + expr.args.sumOf {
                countProperTailSelfCalls(it, selfName, false)
            }
        }
    }

fun countProperTailRecursiveCalls(programText: String): Int {
    val sexpr = parseSExpr(programText)
    val program = toProgram(sexpr)
    return countProperTailSelfCalls(program.body, program.functionName, true)
}