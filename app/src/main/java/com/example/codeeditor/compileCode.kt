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
    // 1. Save the Kotlin file internally
    fileManager.saveFile(fileName, code)
    val internalFile = File(context.filesDir, fileName)
    val externalDir = context.getExternalFilesDir(null) // app-specific external folder
    val externalFile = File(externalDir, fileName)

    if (internalFile.exists()) {
        internalFile.copyTo(externalFile, overwrite = true)
    }

    // 2. Show instructions via onResult
    val instructions = "Kotlin file saved at $externalFile "

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
