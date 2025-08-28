package com.example.codeeditor

data class SyntaxError(
    val message: String,
    val lineNumber: Int,
    val column: Int,
    val errorType: String
)

class SyntaxErrorHandler {
    
    fun checkSyntaxErrors(code: String, fileName: String): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        
        when {
            fileName.endsWith(".py") -> errors.addAll(checkPythonSyntax(code))
            fileName.endsWith(".java") -> errors.addAll(checkJavaSyntax(code))
            fileName.endsWith(".kt") -> errors.addAll(checkKotlinSyntax(code))
            else -> errors.addAll(checkPythonSyntax(code)) // default fallback
        }
        
        return errors
    }
    
    private fun checkPythonSyntax(code: String): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        val lines = code.split("\n")
        
        // Check for unmatched brackets
        val bracketErrors = checkBrackets(code, lines)
        errors.addAll(bracketErrors)
        
        // Check for unmatched quotes
        val quoteErrors = checkQuotes(code, lines)
        errors.addAll(quoteErrors)
        
        // Check for missing colons after if/for/while/def/class
        val colonErrors = checkPythonColons(code, lines)
        errors.addAll(colonErrors)
        
        return errors
    }
    
    private fun checkJavaSyntax(code: String): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        val lines = code.split("\n")
        
        // Check for unmatched brackets
        val bracketErrors = checkBrackets(code, lines)
        errors.addAll(bracketErrors)
        
        // Check for unmatched quotes
        val quoteErrors = checkQuotes(code, lines)
        errors.addAll(quoteErrors)
        
        // Check for missing semicolons
        val semicolonErrors = checkJavaSemicolons(code, lines)
        errors.addAll(semicolonErrors)
        
        return errors
    }
    
    private fun checkKotlinSyntax(code: String): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        val lines = code.split("\n")
        
        // Check for unmatched brackets
        val bracketErrors = checkBrackets(code, lines)
        errors.addAll(bracketErrors)
        
        // Check for unmatched quotes
        val quoteErrors = checkQuotes(code, lines)
        errors.addAll(quoteErrors)
        
        // Check for missing semicolons (optional in Kotlin but common)
        val semicolonErrors = checkKotlinSemicolons(code, lines)
        errors.addAll(semicolonErrors)
        
        return errors
    }
    
    private fun checkBrackets(code: String, lines: List<String>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        val stack = mutableListOf<Pair<Char, Int>>()
        
        for ((lineIndex, line) in lines.withIndex()) {
            for ((charIndex, char) in line.withIndex()) {
                when (char) {
                    '{', '(', '[' -> {
                        stack.add(Pair(char, lineIndex + 1))
                    }
                    '}', ')', ']' -> {
                        if (stack.isEmpty()) {
                            errors.add(SyntaxError(
                                "Unexpected closing bracket '$char'",
                                lineIndex + 1,
                                charIndex + 1,
                                "UnmatchedBracket"
                            ))
                        } else {
                            val (openBracket, _) = stack.removeLast()
                            if (!isMatchingBracket(openBracket, char)) {
                                errors.add(SyntaxError(
                                    "Mismatched brackets: expected '${getMatchingBracket(openBracket)}' but found '$char'",
                                    lineIndex + 1,
                                    charIndex + 1,
                                    "MismatchedBracket"
                                ))
                            }
                        }
                    }
                }
            }
        }
        
        // Check for unclosed brackets
        for ((openBracket, lineNum) in stack) {
            errors.add(SyntaxError(
                "Missing closing bracket '${getMatchingBracket(openBracket)}'",
                lineNum,
                1,
                "MissingBracket"
            ))
        }
        
        return errors
    }
    
    private fun checkQuotes(code: String, lines: List<String>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        
        for ((lineIndex, line) in lines.withIndex()) {
            var inSingleQuote = false
            var inDoubleQuote = false
            var escapeNext = false
            
            for ((charIndex, char) in line.withIndex()) {
                if (escapeNext) {
                    escapeNext = false
                    continue
                }
                
                when (char) {
                    '\\' -> escapeNext = true
                    '\'' -> {
                        if (!inDoubleQuote) {
                            inSingleQuote = !inSingleQuote
                        }
                    }
                    '"' -> {
                        if (!inSingleQuote) {
                            inDoubleQuote = !inDoubleQuote
                        }
                    }
                }
            }
            
            if (inSingleQuote) {
                errors.add(SyntaxError(
                    "Unclosed single quote",
                    lineIndex + 1,
                    line.length,
                    "UnclosedQuote"
                ))
            }
            
            if (inDoubleQuote) {
                errors.add(SyntaxError(
                    "Unclosed double quote",
                    lineIndex + 1,
                    line.length,
                    "UnclosedQuote"
                ))
            }
        }
        
        return errors
    }
    
    private fun checkPythonColons(code: String, lines: List<String>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        
        for ((lineIndex, line) in lines.withIndex()) {
            val trimmedLine = line.trim()
            
            // Check for missing colons after control structures
            if (trimmedLine.startsWith("if ") && !trimmedLine.endsWith(":")) {
                errors.add(SyntaxError(
                    "Missing colon after 'if' statement",
                    lineIndex + 1,
                    trimmedLine.length,
                    "MissingColon"
                ))
            }
            
            if (trimmedLine.startsWith("for ") && !trimmedLine.endsWith(":")) {
                errors.add(SyntaxError(
                    "Missing colon after 'for' statement",
                    lineIndex + 1,
                    trimmedLine.length,
                    "MissingColon"
                ))
            }
            
            if (trimmedLine.startsWith("while ") && !trimmedLine.endsWith(":")) {
                errors.add(SyntaxError(
                    "Missing colon after 'while' statement",
                    lineIndex + 1,
                    trimmedLine.length,
                    "MissingColon"
                ))
            }
            
            if (trimmedLine.startsWith("def ") && !trimmedLine.endsWith(":")) {
                errors.add(SyntaxError(
                    "Missing colon after function definition",
                    lineIndex + 1,
                    trimmedLine.length,
                    "MissingColon"
                ))
            }
            
            if (trimmedLine.startsWith("class ") && !trimmedLine.endsWith(":")) {
                errors.add(SyntaxError(
                    "Missing colon after class definition",
                    lineIndex + 1,
                    trimmedLine.length,
                    "MissingColon"
                ))
            }
        }
        
        return errors
    }
    
    private fun checkJavaSemicolons(code: String, lines: List<String>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        
        for ((lineIndex, line) in lines.withIndex()) {
            val trimmedLine = line.trim()
            
            // Skip empty lines, comments, and control structures
            if (trimmedLine.isEmpty() || 
                trimmedLine.startsWith("//") || 
                trimmedLine.startsWith("/*") ||
                trimmedLine.startsWith("if ") ||
                trimmedLine.startsWith("for ") ||
                trimmedLine.startsWith("while ") ||
                trimmedLine.startsWith("switch ") ||
                trimmedLine.startsWith("try ") ||
                trimmedLine.startsWith("catch ") ||
                trimmedLine.startsWith("finally ") ||
                trimmedLine.startsWith("}") ||
                trimmedLine.startsWith("{")) {
                continue
            }
            
            // Check for missing semicolons on statements
            if (trimmedLine.contains("=") || 
                trimmedLine.contains("return") ||
                trimmedLine.contains("break") ||
                trimmedLine.contains("continue") ||
                trimmedLine.contains("throw")) {
                
                if (!trimmedLine.endsWith(";") && !trimmedLine.endsWith("{")) {
                    errors.add(SyntaxError(
                        "Missing semicolon",
                        lineIndex + 1,
                        trimmedLine.length,
                        "MissingSemicolon"
                    ))
                }
            }
        }
        
        return errors
    }
    
    private fun checkKotlinSemicolons(code: String, lines: List<String>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        
        for ((lineIndex, line) in lines.withIndex()) {
            val trimmedLine = line.trim()
            
            // Skip empty lines, comments, and control structures
            if (trimmedLine.isEmpty() || 
                trimmedLine.startsWith("//") || 
                trimmedLine.startsWith("/*") ||
                trimmedLine.startsWith("if ") ||
                trimmedLine.startsWith("for ") ||
                trimmedLine.startsWith("while ") ||
                trimmedLine.startsWith("when ") ||
                trimmedLine.startsWith("try ") ||
                trimmedLine.startsWith("catch ") ||
                trimmedLine.startsWith("finally ") ||
                trimmedLine.startsWith("}") ||
                trimmedLine.startsWith("{")) {
                continue
            }
            
            // Check for missing semicolons on statements (optional but common)
            if (trimmedLine.contains("=") || 
                trimmedLine.contains("return") ||
                trimmedLine.contains("break") ||
                trimmedLine.contains("continue") ||
                trimmedLine.contains("throw")) {
                
                if (!trimmedLine.endsWith(";") && !trimmedLine.endsWith("{")) {
                    // In Kotlin, semicolons are optional, so this is just a warning
                    errors.add(SyntaxError(
                        "Consider adding semicolon (optional in Kotlin)",
                        lineIndex + 1,
                        trimmedLine.length,
                        "OptionalSemicolon"
                    ))
                }
            }
        }
        
        return errors
    }
    
    private fun isMatchingBracket(open: Char, close: Char): Boolean {
        return (open == '{' && close == '}') ||
               (open == '(' && close == ')') ||
               (open == '[' && close == ']')
    }
    
    private fun getMatchingBracket(open: Char): Char {
        return when (open) {
            '{' -> '}'
            '(' -> ')'
            '[' -> ']'
            else -> ' '
        }
    }
}
