package io.orbyt.adapter.http

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.orbyt.domain.model.Signal
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.domain.model.Greeting
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class BlackHoleClient: CommunicationGateway {

    override fun transmitSignal(pInfo: List<Signal>): Response {
        val json = jsonMapper().writeValueAsString(pInfo)

        val request = Request.Builder()
            .url("http://localhost:8081/signal")
            .post(json.toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123")
            .build()

        val response = OkHttpClient.Builder().build()
            .newCall(request).execute()

        return response
    }

    override fun pointAntenna(pInfo: Greeting): Response {
        val json = jsonMapper().writeValueAsString(pInfo)

        val request = Request.Builder()
            .url("http://localhost:8081/greeting")
            .post(json.toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123")
            .build()

        val response = OkHttpClient.Builder().build()
            .newCall(request).execute()

        return response
    }
}