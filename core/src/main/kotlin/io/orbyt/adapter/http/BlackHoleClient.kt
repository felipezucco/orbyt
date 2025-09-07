package io.orbyt.adapter.http

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.orbyt.domain.model.Signal
import io.orbyt.library.port.out.CommunicationGateway
import io.orbyt.domain.model.Greeting
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resumeWithException

class BlackHoleClient: CommunicationGateway {

    private val client = OkHttpClient
        .Builder()
        .build()

    suspend fun OkHttpClient.await(request: Request): Response =
        suspendCancellableCoroutine { cont ->
            newCall(request)
                .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isCancelled) return
                    cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    cont.resume(response) {}
                }
            })
        }

    override suspend fun transmitSignal(pInfo: List<Signal>): Response {
        val json = jsonMapper().writeValueAsString(pInfo)

        val request = Request.Builder()
            .url("http://localhost:8081/signal")
            .post(json.toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123")
            .build()


        return client.await(request)
    }

    override suspend fun pointAntenna(pInfo: Greeting): Response {
        val json = jsonMapper().writeValueAsString(pInfo)

        val request = Request.Builder()
            .url("http://localhost:8081/greeting")
            .post(json.toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123")
            .build()

        return client.await(request)
    }
}