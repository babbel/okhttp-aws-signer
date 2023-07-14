package com.babbel.mobile.android.commons.okhttpawssigner.testhelpers

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

inline fun request(creator: Request.Builder.() -> Unit): Request {
    val builder = Request.Builder()
    builder.creator()
    return builder.build()
}

var Request.Builder.url: String
    get() = throw RuntimeException("Cannot get url from request as property")
    set(value) {
        val httpUrl = value.toHttpUrl()
        url(httpUrl)
        addHeader("Host", httpUrl.host)
    }

var Request.Builder.headers: Map<String, String>
    get() = throw RuntimeException("Cannot get headers from request as property")
    set(headerMap) {
        headerMap.forEach { (key, value) ->
            addHeader(key, value)
        }
    }
