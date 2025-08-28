/**
 * This is a comprehensive Kotlin sample file
 * demonstrating enhanced comment highlighting
 * 
 * Features:
 * - Single-line comments
 * - Multi-line comments
 * - KDoc comments
 * - Mixed code and comments
 */
fun main() {
    // This is a single line comment
    val greeting = "Hello, Kotlin!"
    println(greeting)
    
    /*
     * This is a multi-line comment
     * It spans multiple lines
     * and demonstrates block comment highlighting
     */
    
    // Variables and types
    val number: Int = 42
    val message = "The answer is: $number"
    val isCorrect = true
    
    /* 
     * Control structures section
     * This comment explains the following code block
     */
    if (isCorrect) {
        println(message)
    } else {
        println("Something went wrong")
    }
    
    // Loop with inline comment
    for (i in 0..2) { // Loop counter
        println("Count: $i")
    }
    
    /*
     * Exception handling demonstration
     * Shows how to handle runtime errors
     */
    try {
        val result = number / 0 // This will cause an error
    } catch (e: ArithmeticException) {
        System.err.println("Division by zero error")
    }
    
    // Function with default parameters
    fun greet(name: String = "World") {
        println("Hello, $name!")
    }
    
    greet()
    greet("Kotlin")
    
    /**
     * Data class demonstration
     * Shows Kotlin's data class feature
     */
    data class Person(val name: String, val age: Int)
    val person = Person("John", 30)
    println("${person.name} is ${person.age} years old")
    
    // End of main function
}

/**
 * Utility function to format text
 * @param text input text to format
 * @return formatted text
 */
fun formatText(text: String): String {
    // Simple text formatting
    return text.uppercase()
}
