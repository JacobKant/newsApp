package ru.jacobkant.newsapp.presentation.newsList

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import moxy.MvpPresenter
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.jacobkant.newsapp.data.NewsRepository
import ru.jacobkant.newsapp.newsApi.Article
import ru.jacobkant.newsapp.presentation.Screens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

interface NewsListView : MvpView {
    @AddToEndSingle
    fun showListState(state: PaginatorState)
}

class NewsListPresenter @Inject constructor(
    private val newsRepository: NewsRepository,
    private val router: Router
) : MvpPresenter<NewsListView>() {
    private val compositeDisposable = CompositeDisposable()

    private val paginator = NewsPaginator(newsRepository::getNewsPage)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable.add(
            paginator.getListUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state -> onChangeStatePaging(state) }
        )
        paginator.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun onSelectCategory(category: String) {
        if (paginator.category == category) return
        val categoryReq = if (category == "All") null else category
        paginator.refresh(categoryReq)
    }

    fun onClickArticle(article: Article) {
        router.navigateTo(Screens.NewsDetailsScreen(articleUrl = article.url))
    }

    fun onScrollToNextPage() {
        paginator.loadMore()
    }

    fun onSwipeRefresh() {
        paginator.refresh()
    }

    private fun onChangeStatePaging(state: PaginatorState) {
        viewState.showListState(state)
    }

}