import me.alllex.parsus.parser.*
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken

sealed interface SExpr {
    data class Atom(val value: String) : SExpr
    data class ListExpr(val items: List<SExpr>) : SExpr
}

object SExprGrammar : Grammar<SExpr>() {
    init {
        regexToken("\\s+", ignored = true)
    }

    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val atom by regexToken("[^\\s()]+") map { SExpr.Atom(it.text) }

    val listItems by parser { repeatZeroOrMore(sexpr) }
    val list by -lpar * listItems * -rpar map { SExpr.ListExpr(it) }

    val sexpr: Parser<SExpr> by list or atom
    override val root by sexpr
}

fun parseSExpr(input: String): SExpr =
    SExprGrammar.parseOrThrow(input)