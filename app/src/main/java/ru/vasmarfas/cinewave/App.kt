package ru.vasmarfas.cinewave

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import ru.vasmarfas.cinewave.data.db.AppDatabase

@HiltAndroidApp
class App: Application() {
    lateinit var db: AppDatabase


    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)


        FirebaseMessaging.getInstance().subscribeToTopic("allDevicesNotifications")


//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "database"
//        )
//            .build()
    }
    fun getInstance(context: Context): AppDatabase
    {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "database"
        )
            .build()
        return db
    }

}