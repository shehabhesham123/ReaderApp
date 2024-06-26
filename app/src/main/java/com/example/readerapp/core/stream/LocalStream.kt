package com.example.readerapp.core.stream

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * this class contains all operations for reading and writing from phone local storage
 */

class LocalStream private constructor() {

    companion object {
        // singleton
        private val stream = LocalStream()
        fun instance(): LocalStream {
            return stream
        }
    }

    /**
     * create folder if this folder not exists else return dir of this folder
     */
    suspend fun folder(folderName: String): File? {
        if (isExternalStorageWritable()) {
            // to get the document path (/storage/emulated/0/Documents)
            val documentsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val dir = File(documentsDir, folderName)
            return if (dir.exists() || dir.mkdirs()) {
                dir
            } else null
        }
        return null
    }

    suspend fun writeFile(
        file: File?,
        fileContent: String,
        append: Boolean
    ): File? {
        return if (file != null) {
            try {
                FileOutputStream(file, append).use { fileOutputStream ->
                    fileOutputStream.write(fileContent.toByteArray())
                }
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null // Return null if file creation failed
            }
        } else null
    }

    fun getFile(
        fileName: String,
        folder: File?
    ): File? {
        return if (folder != null && folder.isDirectory) {
            File(folder, fileName)
        } else null
    }

    suspend fun readFileContent(file: File?): String {
        val stringBuilder = StringBuilder()
        BufferedReader(FileReader(file)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        }
        return stringBuilder.toString()
    }

    suspend fun readFileContent(filePath: String): String {
        val stringBuilder = StringBuilder()
        BufferedReader(FileReader(filePath)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        }
        return stringBuilder.toString()
    }

    fun readFileContent(context: Context, file: Uri?): String? {
        val stringBuilder = StringBuilder()
        return if (file != null) {
            if (getFileExtension(file.path!!) != ".json") return null
            context.contentResolver.openInputStream(file).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream!!)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line).append("\n")
                    }
                }
            }
            stringBuilder.toString()
        } else null
    }

    fun getAllFiles(folder: File?): List<File> {
        return if (folder != null && folder.isDirectory) {
            folder.listFiles()?.filter { it.isFile }?.toList()!!
        } else listOf()
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun getFileExtension(filePath: String): String {
        var extension = ""
        try {
            extension = filePath.substring(filePath.lastIndexOf("."))
        } catch (exception: Exception) {
            Log.i("Error", extension)
        }
        return extension
    }
}