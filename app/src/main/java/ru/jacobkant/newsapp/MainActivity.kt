package ru.jacobkant.newsapp

import android.os.Bundle
import moxy.MvpAppCompatActivity
import ru.jacobkant.newsapp.presentation.Screens
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    private val navigator: Navigator = SupportAppNavigator(this, R.id.activity_main_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.ComponentHolder.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // todo connect cicerone
        if (savedInstanceState == null) {
            router.newRootScreen(Screens.NewsListScreen())
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}
