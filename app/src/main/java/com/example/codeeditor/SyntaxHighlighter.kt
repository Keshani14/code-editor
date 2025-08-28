package com.example.codeeditor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

class SyntaxHighlighter {
    
    fun highlightCode(text: String, syntaxRules: SyntaxRules, isDarkMode: Boolean): AnnotatedString {
        return buildAnnotatedString {
            val lines = text.split("\n")
            
            // First pass: find all multi-line comment ranges
            val multiLineCommentRanges = findMultiLineCommentRanges(text, syntaxRules.comments)
            
            lines.forEachIndexed { lineIndex, line ->
                if (lineIndex > 0) append("\n")
                
                val lineStartPos = lines.take(lineIndex).sumOf { it.length + 1 } // +1 for newline
                val lineEndPos = lineStartPos + line.length
                
                val highlightedLine = highlightLine(line, syntaxRules, isDarkMode, lineStartPos, multiLineCommentRanges)
                append(highlightedLine)
            }
        }
    }
    
    private fun findMultiLineCommentRanges(text: String, commentMarkers: List<String>): List<Triple<Int, Int, String>> {
        val ranges = mutableListOf<Triple<Int, Int, String>>()
        
        commentMarkers.forEach { marker ->
            when (marker) {
                "/*" -> {
                    var startIndex = 0
                    while (true) {
                        val startPos = text.indexOf(marker, startIndex)
                        if (startPos == -1) break
                        
                        val endPos = text.indexOf("*/", startPos)
                        if (endPos != -1) {
                            ranges.add(Triple(startPos, endPos + 2, "comment"))
                            startIndex = endPos + 2
                        } else {
                            // Unclosed multi-line comment, highlight to end
                            ranges.add(Triple(startPos, text.length, "comment"))
                            break
                        }
                    }
                }
            }
        }
        
        return ranges
    }
    
    private fun highlightLine(line: String, syntaxRules: SyntaxRules, isDarkMode: Boolean, lineStartPos: Int, multiLineCommentRanges: List<Triple<Int, Int, String>>): AnnotatedString {
        return buildAnnotatedString {
            var currentIndex = 0
            
            // Check if this line is part of a multi-line comment
            val lineEndPos = lineStartPos + line.length
            val isInMultiLineComment = multiLineCommentRanges.any { (start, end, _) ->
                lineStartPos < end && lineEndPos > start
            }
            
            if (isInMultiLineComment) {
                // Entire line is part of a multi-line comment
                pushStyle(SpanStyle(color = getCommentColor(isDarkMode)))
                append(line)
                pop()
                return@buildAnnotatedString
            }
            
            // Handle single-line comments first (they have highest priority)
            val commentRanges = findSingleLineCommentRanges(line, syntaxRules.comments)
            
            // Handle strings
            val stringRanges = findStringRanges(line, syntaxRules.strings)
            
            // Handle keywords, types, and modifiers
            val keywordRanges = findKeywordRanges(line, syntaxRules.keywords)
            val typeRanges = findKeywordRanges(line, syntaxRules.types)
            val modifierRanges = findKeywordRanges(line, syntaxRules.modifiers)
            
            // Combine all ranges and sort by start position
            val allRanges = (commentRanges + stringRanges + keywordRanges + typeRanges + modifierRanges)
                .sortedBy { it.first }
            
            // Apply highlighting
            var lastEnd = 0
            allRanges.forEach { (start, end, type) ->
                // Add text before this range
                if (start > lastEnd) {
                    append(line.substring(lastEnd, start))
                }
                
                // Add highlighted text
                val textToHighlight = line.substring(start, end)
                when (type) {
                    "comment" -> pushStyle(SpanStyle(color = getCommentColor(isDarkMode)))
                    "string" -> pushStyle(SpanStyle(color = getStringColor(isDarkMode)))
                    "keyword" -> pushStyle(SpanStyle(color = getKeywordColor(isDarkMode), fontWeight = FontWeight.Bold))
                    "type" -> pushStyle(SpanStyle(color = getTypeColor(isDarkMode), fontWeight = FontWeight.Bold))
                    "modifier" -> pushStyle(SpanStyle(color = getModifierColor(isDarkMode), fontWeight = FontWeight.Bold))
                }
                append(textToHighlight)
                pop()
                
                lastEnd = end
            }
            
            // Add remaining text
            if (lastEnd < line.length) {
                append(line.substring(lastEnd))
            }
        }
    }
    
    private fun findSingleLineCommentRanges(line: String, commentMarkers: List<String>): List<Triple<Int, Int, String>> {
        val ranges = mutableListOf<Triple<Int, Int, String>>()
        
        commentMarkers.forEach { marker ->
            when (marker) {
                "//" -> {
                    val index = line.indexOf(marker)
                    if (index != -1) {
                        ranges.add(Triple(index, line.length, "comment"))
                    }
                }
            }
        }
        
        return ranges
    }
    
    private fun findStringRanges(line: String, stringDelimiters: List<String>): List<Triple<Int, Int, String>> {
        val ranges = mutableListOf<Triple<Int, Int, String>>()
        
        stringDelimiters.forEach { delimiter ->
            var startIndex = 0
            while (true) {
                val index = line.indexOf(delimiter, startIndex)
                if (index == -1) break
                
                val endIndex = line.indexOf(delimiter, index + 1)
                if (endIndex != -1) {
                    ranges.add(Triple(index, endIndex + 1, "string"))
                    startIndex = endIndex + 1
                } else {
                    // Unclosed string, highlight to end of line
                    ranges.add(Triple(index, line.length, "string"))
                    break
                }
            }
        }
        
        return ranges
    }
    
    private fun findKeywordRanges(line: String, keywords: List<String>): List<Triple<Int, Int, String>> {
        val ranges = mutableListOf<Triple<Int, Int, String>>()
        
        keywords.forEach { keyword ->
            var startIndex = 0
            while (true) {
                val index = line.indexOf(keyword, startIndex, ignoreCase = true)
                if (index == -1) break
                
                // Check if it's a whole word
                val beforeChar = if (index > 0) line[index - 1] else ' '
                val afterChar = if (index + keyword.length < line.length) line[index + keyword.length] else ' '
                
                if (!beforeChar.isLetterOrDigit() && beforeChar != '_' && 
                    !afterChar.isLetterOrDigit() && afterChar != '_') {
                    ranges.add(Triple(index, index + keyword.length, "keyword"))
                }
                
                startIndex = index + 1
            }
        }
        
        return ranges
    }
    
    private fun getCommentColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color(0xFF6A9955) else Color(0xFF008000)
    }
    
    private fun getStringColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color(0xFFCE9178) else Color(0xFFA31515)
    }
    
    private fun getKeywordColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color(0xFF569CD6) else Color(0xFF0000FF)
    }
    
    private fun getTypeColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color(0xFF4EC9B0) else Color(0xFF267f99)
    }
    
    private fun getModifierColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color(0xFFDCDCAA) else Color(0xFF000080)
    }
}
