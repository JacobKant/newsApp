package ru.jacobkant.newsapp.di

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.jacobkant.newsapp.App
import ru.jacobkant.newsapp.MainActivity
import ru.jacobkant.newsapp.newsApi.NewsApi
import ru.jacobkant.newsapp.presentation.newsList.NewsListFragment
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(newsFragment: MainActivity)
    fun inject(newsListFragment: NewsListFragment)
}

@Module
class AppModule(private val app: App) {

    private val cicerone: Cicerone<Router> = Cicerone.create()

    @Provides
    fun context(): Context = app

    @Provides
    @Singleton
    fun newsApi(): NewsApi = NewsApi.create()

    @Provides
    @Singleton
    fun router(): Router = cicerone.router

    @Provides
    @Singleton
    fun navigatorHolder(): NavigatorHolder = cicerone.navigatorHolder

}