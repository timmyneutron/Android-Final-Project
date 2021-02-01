package com.timmyneutron.finalproject.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface NYTimesAPI {
    // This function needs to be called from a coroutine, hence the suspend
    // in its type.  Also note the @Query annotation, which says that when
    // called, retrofit will add "&difficulty=%s".format(level) to the URL
    // Thanks, retrofit!
    // Hardcode several parameters in the GET for simplicity
    // So URL can have & and ? characters

    // api key removed for security
    @GET("svc/search/v2/articlesearch.json?api-key=")
    suspend fun fetchHeadlines(@Query("q") level: String) : NYTimesResponse

    data class NYTimesResponse(val response: NYTimesDocsList)
    data class NYTimesDocsList(val docs: List<NYTimesArticle>)
    data class NYTimesArticle(val web_url: String, val headline: NYTimesHeadline)
    data class NYTimesHeadline(val main: String)
    data class urlHeadline(val url: String, val headline: String)

    companion object {
        // Leave this as a simple, base URL.  That way, we can have many different
        // functions (above) that access different "paths" on this server
        // https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("api.nytimes.com")
            .build()

        // Public create function that ties together building the base
        // URL and the private create function that initializes Retrofit
        fun create(): NYTimesAPI = create(url)
        private fun create(httpUrl: HttpUrl): NYTimesAPI {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NYTimesAPI::class.java)
        }
    }
}