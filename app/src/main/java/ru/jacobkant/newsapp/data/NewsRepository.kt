package ru.jacobkant.newsapp.data

import io.reactivex.Single
import ru.jacobkant.newsapp.newsApi.NewsApi
import ru.jacobkant.newsapp.newsApi.TopHeadlinesResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val api: NewsApi) {
    fun getNewsPage(category: String?, pageNumber: Int): Single<TopHeadlinesResponse> {
        return api.getTopHeadlines(category, pageNumber)
    }
}