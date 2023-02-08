package com.riteshkumar.backgroundlocationupdate.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockClientInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {

        requestCounter++
        val statusCode: Int
        val response = if (requestCounter > 5) {
            requestCounter = 1
            statusCode = statusCodeFailure
            responseFailed
        } else {
            statusCode = statusCodeSuccess
            responseSuccess
        }

        return Response.Builder()
            .code(statusCode)
            .message(response)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(response.toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }

    companion object {
        var requestCounter = 1
        val statusCodeSuccess = 200
        val statusCodeFailure = 400
        val responseSuccess = "{\n" +
            "  \"status\": \"success\",\n" +
            "  \"statusCode\": 200\n" +
            "}"

        val responseFailed = "{\n" +
            "  \"status\": \"failed\",\n" +
            "  \"statusCode\": 400\n" +
            "}"
    }
}