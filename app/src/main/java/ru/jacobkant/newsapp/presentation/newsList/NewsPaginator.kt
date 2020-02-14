package ru.jacobkant.newsapp.presentation.newsList

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import ru.jacobkant.newsapp.newsApi.Article
import ru.jacobkant.newsapp.newsApi.TopHeadlinesResponse
import kotlin.math.ceil

enum class StateStatus {
    Empty, EmptyProgress, EmptyError, Data, Refreshing, NewPageProgress, FullDataLoaded
}

data class PaginatorState(
    val status: StateStatus = StateStatus.Empty,
    val data: List<Article> = listOf(),
    val totalPages: Int = 0,
    val loadedPagesCount: Int = 0,
    val lastError: Throwable? = null
)

class NewsPaginator(
    private val pageRequest: (String?, Int) -> Single<TopHeadlinesResponse>,
    private var currentCategory: String? = null
) {
    private val defaultPageSize: Int = 20
    private val compositeDisposable = CompositeDisposable()
    private val state: BehaviorSubject<PaginatorState> =
        BehaviorSubject.createDefault(PaginatorState())

    val category: String?
        get() = currentCategory

    private val currentState
        get() = state.value!!

    fun getListUpdates(): Observable<PaginatorState> {
        return state.doOnDispose { compositeDisposable.clear() }
    }

    fun refresh(category: String? = currentCategory) {
        requestPage(category, 1, true)
    }

    fun loadMore() {
        if (currentState.status == StateStatus.NewPageProgress) return
        requestPage(currentCategory, currentState.loadedPagesCount + 1, false)
    }

    private fun requestPage(category: String?, pageNumber: Int, isRefresh: Boolean) {
        compositeDisposable.clear()
        this.currentCategory = category
        compositeDisposable.add(
            pageRequest(category, pageNumber)
                .doOnSubscribe {
                    state.onNext(
                        currentState.copy(
                            status = when {
                                currentState.data.isEmpty() -> StateStatus.EmptyProgress
                                isRefresh -> StateStatus.Refreshing
                                else -> StateStatus.NewPageProgress
                            }
                        )
                    )
                }
                .subscribe({
                    val totalPages = ceil(it.totalResults.toFloat() / defaultPageSize).toInt()
                    state.onNext(
                        currentState.copy(
                            status = if (pageNumber == totalPages) StateStatus.FullDataLoaded else StateStatus.Data,
                            totalPages = totalPages,
                            loadedPagesCount = pageNumber,
                            data = if (isRefresh) it.articles else currentState.data + it.articles
                        )
                    )
                }, {
                    state.onNext(
                        currentState.copy(
                            status = if (currentState.data.isNotEmpty()) StateStatus.Data else StateStatus.EmptyError,
                            lastError = it
                        )
                    )
                })
        )
    }


}