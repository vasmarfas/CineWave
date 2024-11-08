package ru.vasmarfas.cinewave.data.retrofit

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
//gamestop
private const val API_KEY_1 = "TBD"
//tancher
private const val API_KEY_2 = "TBD"
//gmailmain
private const val API_KEY_3 = "TBD"
//bodrenoks
private const val API_KEY_4 = "TBD"
//dota
private const val API_KEY_5 = "TBD"
//sauros
private const val API_KEY_6 = "TBD"
//vpnor
private const val API_KEY_7 = "TBD"
//secvpn
private const val API_KEY_8 = "TBD"

class APIKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        val workingkey = runBlocking { getWorkingApiKey() }

//        //Log.d("DEBUG INTERCEPTOR", workingkey)
        currentRequest.addHeader("X-API-KEY", workingkey).build()
        val newRequest = currentRequest.build()

        return chain.proceed(newRequest)
    }

    private suspend fun getWorkingApiKey(): String {
        var out_key = ""

        val allKeys = mutableListOf(
            API_KEY_1,
            API_KEY_2,
            API_KEY_3,
            API_KEY_4,
            API_KEY_5,
            API_KEY_6,
            API_KEY_7,
            API_KEY_8
        )

        var counter = 0
        while (counter < 5) {
            if (out_key != "") {
                break
            }
            for (apiKey in allKeys) {
                val result = runBlocking {
                    KinopoiskKeyAPI.RetrofitInstance.getKeyAPI.getApiKeyInfo(
                        key = apiKey,
                        api_key = apiKey
                    )
                }
                if (result.isSuccessful) {
//                //Log.d("DEBUG INTERCEPTOR", result.toString())

                    val keyInfo = result.body()
                    if (keyInfo != null) {
                        if (keyInfo.dailyQuota != null) {
                            val remainingUsages = keyInfo.dailyQuota.value - keyInfo.dailyQuota.used
                            if (remainingUsages > 30) {
                                out_key = apiKey
                                break
                            }
                        }
                    } else {
//                    //Log.d("DEBUG INTERCEPTOR", result.toString())

                    }
                } else {
//                //Log.d("DEBUG INTERCEPTOR", result.toString())

                }
            }
            counter++
        }
        return out_key
    }
}