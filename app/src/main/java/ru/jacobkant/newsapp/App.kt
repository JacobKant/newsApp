package ru.jacobkant.newsapp

import android.app.Application
import com.facebook.stetho.Stetho
import ru.jacobkant.newsapp.di.AppComponent
import ru.jacobkant.newsapp.di.AppModule
import ru.jacobkant.newsapp.di.DaggerAppComponent

class App : Application() {
    object ComponentHolder {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        ComponentHolder.appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}