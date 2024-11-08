package ru.vasmarfas.cinewave.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vasmarfas.cinewave.data.db.AppDatabase
import ru.vasmarfas.cinewave.data.db.UserDao
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DIDatabaseModule {

    @Provides
    @Singleton
    fun injectRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()

    @Provides
    @Singleton
    fun injectDao(
        database: AppDatabase
    ): UserDao = database.userDao()

}