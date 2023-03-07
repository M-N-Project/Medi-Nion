package com.example.medi_nion

import android.util.Log
import androidx.annotation.GuardedBy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class Upload_Request(
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
    private val params: MutableMap<String, String>
) : Request<String>(method, url, errorListener) {


    private val lock = Any()

    @GuardedBy("lock")
    private var listener: Response.Listener<String>? = listener

    public override fun getParams(): MutableMap<String, String> {
        Log.d("1556", params.toString())
        return params
    }

    override fun cancel() {
        super.cancel()
        synchronized(lock) { listener = null }
    }

    override fun deliverResponse(response: String) {
        var listener: Response.Listener<String>?
        synchronized(lock) { listener = this.listener }
        if (listener != null) {
            listener!!.onResponse(response)
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        val parsed: String = try {
            String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
        } catch (e: UnsupportedEncodingException) {
            String(response.data)
        }

        return Response.success(
            parsed,
            HttpHeaderParser.parseCacheHeaders(response)
        )
    }
}