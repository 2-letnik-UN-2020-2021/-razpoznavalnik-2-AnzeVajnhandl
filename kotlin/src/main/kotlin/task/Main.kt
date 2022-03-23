import task.Scanner
import java.io.File
import java.io.InputStream

data class Token(val value: Int, val lexeme: String, val startRow: Int, val startColumn: Int)

const val FLOAT = 1
const val VARIABLE = 2
const val PLUS = 3
const val MINUS = 4
const val TIMES = 5
const val DIVIDE = 6
const val POW = 7
const val LPAREN = 8
const val RPAREN = 9

class Recognizer(private val scanner: Scanner) {
    private var last: task.Token? = null

    fun recognize(): Boolean {
        last = scanner.getToken()
        val status = recognizeE()
        return if (last == null) status
        else false
    }

    fun recognizeF():Boolean {
        val lookahead = last?.value
                if (lookahead == null) {
                    true
                }
        return when(lookahead) {
            LPAREN -> recognizeTerminal(LPAREN) && recognizeE() && recognizeTerminal(RPAREN)
            FLOAT -> recognizeTerminal(FLOAT)
            VARIABLE -> recognizeTerminal(VARIABLE)
            else -> false
        }
    }
    fun recognizeY():Boolean{
        val lookahead = last?.value
        if (lookahead == null) {
            true
        }
        return when(lookahead) {
            MINUS -> recognizeTerminal(MINUS) && recognizeF()
            PLUS -> recognizeTerminal(PLUS) && recognizeF()
            LPAREN -> recognizeF()
            FLOAT -> recognizeF()
            VARIABLE -> recognizeF()
            else -> false
        }
    }
    fun recognizeX():Boolean = recognizeY() && recognizeX_()
    fun recognizeT():Boolean = recognizeX() && recognizeT_()
    fun recognizeE():Boolean = recognizeT() && recognizeE_()

    fun recognizeE_():Boolean {
        val lookahead = last?.value
        if (lookahead == null) {
            true
        }
        return when(lookahead) {
            PLUS -> recognizeTerminal(PLUS)  && recognizeT() && recognizeE_()
            MINUS -> recognizeTerminal(MINUS) && recognizeT() && recognizeE_()
            else -> false
        }
    }

    fun recognizeT_():Boolean{
        val lookahead = last?.value
        if (lookahead == null) {
            true
        }
        return when(lookahead) {
            TIMES -> recognizeTerminal(TIMES) && recognizeX() && recognizeT_()
            DIVIDE -> recognizeTerminal(DIVIDE) && recognizeX() && recognizeT_()
            else -> false
        }
    }

    fun recognizeX_():Boolean{
        val lookahead = last?.value
        if (lookahead == null) {
            true
        }
        return when(lookahead) {
            POW -> recognizeTerminal(POW) && recognizeX_()
            else -> false
        }
    }

    private fun recognizeTerminal(value: Int) =
        if (last?.value == value) {
            last = scanner.getToken()
            true
        }
        else false
}

fun main(args: Array<String>) {
    val inputStream: InputStream = File(args[0]).inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }
    if (Recognizer(Scanner(task.Example, "+4".byteInputStream())).recognize()) {
        print("accept")
    } else {
        print("reject")
    }
}