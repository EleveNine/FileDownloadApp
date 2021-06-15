package com.elevenine.filedownloadapp

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

fun provideFileApi(okHttpClient: OkHttpClient): FileApi {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://square.github.io/retrofit/") // dummy base url
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
    return retrofit.create(FileApi::class.java)
}

fun provideOkHttpClient(): OkHttpClient {
    val okHttpClientBuilder = OkHttpClient.Builder()

    return okHttpClientBuilder
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .cache(null)
        .build()
}

interface FileApi {
    @GET
    fun downloadFile(
        @Url fileUrl: String
    ): Single<ResponseBody>
}