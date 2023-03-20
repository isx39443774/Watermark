import java.lang.Exception

fun main() {
    // put your code here
    val i1 = readln().toInt()
    val i2 = readln().toInt()

    if (i2 == 0) {
        println("Division by zero, please fix the second argument!")
    } else {
        println(i1 / i2)
    }

}