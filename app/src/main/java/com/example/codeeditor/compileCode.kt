package com.example.codeeditor

import android.content.Context
import java.io.File

fun compileCode(
    context: Context,
    code: String,
    fileManager: FileManager,
    fileName: String,
    onResult: (String) -> Unit
) {
    // 0. Check for syntax errors first
    val syntaxErrorHandler = SyntaxErrorHandler()
    val syntaxErrors = syntaxErrorHandler.checkSyntaxErrors(code, fileName)
    
    if (syntaxErrors.isNotEmpty()) {
        // Build error message
        val errorMessage = buildString {
            append("Compilation failed due to syntax errors:\n\n")
            syntaxErrors.forEach { error ->
                append("Line ${error.lineNumber}: ${error.message}\n")
            }
            append("\nPlease fix these errors before compiling.")
        }
        onResult(errorMessage)
        return
    }
    
    // 1. Save the file internally (no syntax errors found)
    fileManager.saveFile(fileName, code)
    val internalFile = File(context.filesDir, fileName)
    val externalDir = context.getExternalFilesDir(null) // app-specific external folder
    val externalFile = File(externalDir, fileName)

    if (internalFile.exists()) {
        internalFile.copyTo(externalFile, overwrite = true)
    }

    // 2. Show success message via onResult
    val instructions = "File saved successfully at $externalFile "

//          On your PC, do the following manually:

//        1. Push the file to the device:
//            adb shell
//            cd  $externalFile
//            ls
//            exit

//        2. Push the file to the device:
//           adb pull $externalFile C:/Users/<Device name>/Desktop/file name
//        3. Compile it using your script:
//           ./compile_kotlin.sh /sdcard/$fileName

    onResult(instructions)
}
