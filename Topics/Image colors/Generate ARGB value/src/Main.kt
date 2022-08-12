const val MIN = 0
const val MAX = 255
fun printARGB() {
    val (a, r, g, b) = readln().split(" ").map { it.toInt() }
    if (a !in MIN..MAX || r !in MIN..MAX || g !in MIN..MAX || b !in MIN..MAX) {
        println("Invalid input")
    } else {
        val colorAlpha = Color(r, g, b, a)
        println(colorAlpha.rgb.toUInt())
    }
}
