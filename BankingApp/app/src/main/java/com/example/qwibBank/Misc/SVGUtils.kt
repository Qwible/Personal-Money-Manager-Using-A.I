package com.example.qwibBank.Misc

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.pixplicity.sharp.Sharp
import okhttp3.*
import java.io.*


class SVGUtils {
    private var httpClient: OkHttpClient? = null

    fun fetchImage(context: Context, url: String, target: ImageView) {
        val numeric = url.matches("-?\\d+(\\.\\d+)?".toRegex())
        if (numeric){
            val drawable = context.resources.getDrawable(url.toInt())
            target.setImageDrawable(drawable)
        } else {
            val imageStream: InputStream = FileInputStream(url)
            Sharp.loadInputStream(imageStream).into(target)
            imageStream.close()
        }
    }


    fun saveSVG(url: String, context: Context, name : String):String? {
        val file = File(context.dataDir, name + ".svg")

        if (httpClient == null) {
            // Use cache for performance and basic offline capability
            httpClient = OkHttpClient.Builder()
                .cache(Cache(context.getCacheDir(), 5 * 1024 * 1014))
                .build()
        }
        val request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Maybe do something if no image is recovered
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val stream = response.body()?.byteStream()

                try {
                    val out = FileOutputStream(file)
                    var len: Int?
                    val buffer = ByteArray(1024)
                    while (stream?.read(buffer).also {len = it} != (-1)) {
                        len?.let { out.write(buffer, 0, it) }
                    }
                    out.flush()
                    out.close()
                    stream?.close()

                } catch (e: FileNotFoundException) {
                    Log.d("Excep", e.toString())
                } catch (e: IOException) {
                    Log.d("Excep", e.toString())
                }
            }
        })

        return file.absolutePath
    }
}