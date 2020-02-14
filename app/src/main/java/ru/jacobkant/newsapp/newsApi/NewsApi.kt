package ru.jacobkant.newsapp.newsApi

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.jacobkant.newsapp.BuildConfig

enum class NewsCategory(val apiQueryValue: String) {
    @SerializedName("business")
    Business("business"),

    @SerializedName("entertainment")
    Entertainment("entertainment"),

    @SerializedName("general")
    General("general"),

    @SerializedName("health")
    Health("health"),

    @SerializedName("science")
    Science("science"),

    @SerializedName("sports")
    Sports("sports"),

    @SerializedName("technology")
    Technology("technology");

}

data class TopHeadlinesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<Article>
)

data class Article(
    @SerializedName("author")
    val author: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("publishedAt")
    val publishedAt: LocalDateTime,
    @SerializedName("urlToImage")
    val urlToImage: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("source")
    val source: Source
)

data class Source(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String
)

fun localDateTimeDeserializer(formatter: DateTimeFormatter? = DateTimeFormatter.ISO_ZONED_DATE_TIME): JsonDeserializer<LocalDateTime> =
    JsonDeserializer<LocalDateTime> { json, _, _ ->
        if (json == null)
            null
        else
            LocalDateTime.parse(json.asString, formatter)
    }

fun localDateTimeSerializer(formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT): JsonSerializer<LocalDateTime> =
    JsonSerializer { date, _, _ ->
        if (date == null)
            null
        else
            JsonPrimitive(formatter.format(date))
    }


interface NewsApi {

    companion object {
        private const val baseUrl = "https://newsapi.org/v2/"

        fun create(): NewsApi {
            val okHttpBuilder = OkHttpClient.Builder()
                .addInterceptor {
                    val originalReq = it.request()
                    val originalUrl = originalReq.url()
                    val newUrl = originalUrl.newBuilder()
                        .addQueryParameter("apiKey", "70b3bb5da4e14268a355a3e66179c7d8")
                        .addQueryParameter("country", "us")
                        .build()
                    val newReq = originalReq.newBuilder()
                        .url(newUrl)
                        .build()
                    it.proceed(newReq)
                }
            if (BuildConfig.DEBUG) okHttpBuilder.addNetworkInterceptor(StethoInterceptor())

            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val gson = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(
                    LocalDateTime::class.java,
                    localDateTimeDeserializer(dateTimeFormatter)
                )
                .registerTypeAdapter(
                    LocalDateTime::class.java,
                    localDateTimeSerializer(dateTimeFormatter)
                )
                .create()

            return Retrofit.Builder()
                .client(okHttpBuilder.build())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(NewsApi::class.java)
        }
    }

    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("category") category: String?,
        @Query("page") page: Int?
    ): Single<TopHeadlinesResponse>


}



