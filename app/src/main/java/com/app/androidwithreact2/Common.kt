package com.app.androidwithreact2

import android.content.Context
import java.io.File
import java.io.FileWriter


class Common {
    companion object{
        fun readFileFromInternalStorage(context: Context, fileName: String): String {
            var content = ""
            val yourFilePath = context.filesDir.toString() + "/" + fileName
            val yourFile = File(yourFilePath)
            if (yourFile.exists()) {
                content = yourFile.readText()
            }
            return content
        }

        fun writeFileToInternalStorageSyncronously(context: Context, sFileName: String, sBody: String) {
            val dir = File(context.filesDir.toString())
            if (!dir.exists()) {
                dir.mkdir()
            }

            try {
                val gpxfile = File(dir, sFileName)
                val writer = FileWriter(gpxfile, false)
                writer.write(sBody)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}