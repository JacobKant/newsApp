package ru.jacobkant.newsapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.jacobkant.newsapp.presentation.newsDetails.NewsDetailsFragment
import ru.jacobkant.newsapp.presentation.newsList.NewsListFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    class NewsListScreen() : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return NewsListFragment()
        }
    }

    class NewsDetailsScreen(private val articleUrl: String) : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return NewsDetailsFragment().apply {
                val bundle = Bundle()
                bundle.putString("url", articleUrl)
                arguments = bundle
            }
        }
    }
}