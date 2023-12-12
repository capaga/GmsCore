package org.microg.gms.games.signin

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.microg.gms.games.signin.enums.ImageEnum
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object ImageFileDownloader {
    private val TAG = ImageFileDownloader::class.java.simpleName
    private const val TIME_OUT = 5000

    @JvmStatic
    fun generateImageList(url: String): List<String> {
        val i = url.lastIndexOf("=")
        val substring = url.substring(0, i)
        val nameList: MutableList<String> = ArrayList(5)
        nameList.add(substring + "=" + ImageEnum.S88.name)
        nameList.add(substring + "=" + ImageEnum.S96.name)
        nameList.add(substring + "=" + ImageEnum.S132.name)
        nameList.add(substring + "=" + ImageEnum.S176.name)
        nameList.add(substring + "=" + ImageEnum.S330.name)
        return nameList
    }

    @JvmStatic
    fun generateName(): String {
        val builder = StringBuilder()
        val time = Date().time
        builder.append("datadownloadfile_").append(time)
        return builder.toString()
    }

    /**
     * @param fileUrl  download file url
     * @param savePath save image path
     */
    @JvmStatic
    fun downloadFile(fileUrl: String?, savePath: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "----start download ------")
            var `in`: InputStream? = null
            var out: FileOutputStream? = null
            try {
                val url = URL(fileUrl)
                val conn = url.openConnection() as HttpURLConnection

                // Set connection timeout and read timeout
                conn.connectTimeout = TIME_OUT
                conn.readTimeout = TIME_OUT
                // get input stream
                `in` = BufferedInputStream(conn.inputStream)

                // Create output stream
                out = FileOutputStream(savePath)
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    // buffer zone
                    val buffer = ByteArray(1024)
                    var len = 0

                    // read input stream and write output stream
                    while (`in`.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "FileDownloader exception: " + e.message)
            } finally {
                //close input and output streams
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}