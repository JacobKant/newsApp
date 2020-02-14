package ru.jacobkant.newsapp.presentation.newsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.frag_news_list.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.jacobkant.newsapp.App
import ru.jacobkant.newsapp.R
import ru.jacobkant.newsapp.newsApi.Article
import ru.jacobkant.newsapp.newsApi.NewsCategory
import javax.inject.Inject
import javax.inject.Provider

class NewsListFragment : MvpAppCompatFragment(), NewsListView {

    @Inject
    lateinit var presenterProvider: Provider<NewsListPresenter>

    private val presenter: NewsListPresenter by moxyPresenter { presenterProvider.get() }

    private val newsAdapter = NewsAdapter(
        itemClickListener = { presenter.onClickArticle(it) },
        onScrollToLastPageListener = { presenter.onScrollToNextPage() }
    )

    private val categoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            // todo убрать в категории
            listOf("All", *NewsCategory.values().map { it.apiQueryValue }.toTypedArray())
        )
    }

    private val onChangeCategoryListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val category = parent?.adapter?.getItem(position) as String
                presenter.onSelectCategory(category)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.ComponentHolder.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frag_news_list_recycler.adapter = newsAdapter
        frag_news_list_recycler.layoutManager = LinearLayoutManager(requireContext())


        frag_news_list_category.adapter = categoryAdapter
        frag_news_list_category.setSelection(0, false)
        frag_news_list_category.onItemSelectedListener = onChangeCategoryListener

        frag_news_list_refresh.setOnRefreshListener {
            presenter.onSwipeRefresh()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        frag_news_list_progress.isVisible = isLoading
        frag_news_list_recycler.isVisible = !isLoading
    }

    override fun showListState(state: PaginatorState) {
        when (state.status) {
            StateStatus.Empty -> {
                newsAdapter.isFullData = false
                frag_news_list_refresh.isRefreshing = false
                showLoading(false)
                newsAdapter.setItems(state.data)
            }
            StateStatus.EmptyProgress -> {
                newsAdapter.isFullData = false
                frag_news_list_refresh.isRefreshing = false
                showLoading(true)
            }
            StateStatus.NewPageProgress -> {
                newsAdapter.isFullData = false
                frag_news_list_refresh.isRefreshing = false
                showLoading(true)
            }
            StateStatus.Refreshing -> {
                frag_news_list_refresh.isRefreshing = true
            }
            StateStatus.Data -> {
                newsAdapter.isFullData = false
                newsAdapter.setItems(state.data)
                showLoading(false)
                frag_news_list_refresh.isRefreshing = false
            }
            StateStatus.EmptyError -> {
                newsAdapter.isFullData = false
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_LONG).show()
                newsAdapter.setItems(state.data)
                showLoading(false)
                frag_news_list_refresh.isRefreshing = false
            }
            StateStatus.FullDataLoaded -> {
                newsAdapter.isFullData = true
                newsAdapter.setItems(state.data)
                showLoading(false)
                frag_news_list_refresh.isRefreshing = false
            }
        }
    }
}


