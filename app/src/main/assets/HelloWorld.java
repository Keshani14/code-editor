/**
 * This is a comprehensive Java sample file
 * demonstrating enhanced comment highlighting
 * 
 * Features:
 * - Single-line comments
 * - Multi-line comments
 * - JavaDoc comments
 * - Mixed code and comments
 */
public class HelloWorld {
    // This is a single line comment
    private static final String GREETING = "Hello, World!";
    
    /*
     * This is a multi-line comment
     * It spans multiple lines
     * and demonstrates block comment highlighting
     */
    
    /**
     * Main method - entry point of the program
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println(GREETING);
        
        // Variables and types
        int number = 42;
        String message = "The answer is: " + number;
        boolean isCorrect = true;
        
        /* 
         * Control structures section
         * This comment explains the following code block
         */
        if (isCorrect) {
            System.out.println(message);
        } else {
            System.out.println("Something went wrong");
        }
        
        // Loop with inline comment
        for (int i = 0; i < 3; i++) { // Loop counter
            System.out.println("Count: " + i);
        }
        
        /*
         * Exception handling demonstration
         * Shows how to handle runtime errors
         */
        try {
            int result = number / 0; // This will cause an error
        } catch (ArithmeticException e) {
            System.err.println("Division by zero error");
        }
        
        // End of main method
    }
    
    /**
     * Utility method to format text
     * @param text input text to format
     * @return formatted text
     */
    private String formatText(String text) {
        // Simple text formatting
        return text.toUpperCase();
    }
}
