class Vehicle (val name: String) {
    // add name

    // create constructor

    // create inner class Body
    inner class Body (val color:String) {
        fun printColor() {
            println("The $name vehicle has a $color body.")
        }
    }
}

/*
class Vehicle {
    val name: String

    constructor(name: String) {
        this.name = name
    }

    inner class Body {
        val color: String

        constructor(color: String) {
            this.color = color
        }

        fun printColor() {
            println("The $name vehicle has a $color body.")
        }
    }
}
 */