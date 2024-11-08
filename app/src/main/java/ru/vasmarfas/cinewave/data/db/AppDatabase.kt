package ru.vasmarfas.cinewave.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.vasmarfas.cinewave.data.db.entity.AuthTokenModel
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.db.entity.DataConverter
import ru.vasmarfas.cinewave.data.db.entity.FilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.FilmPersonInfoModel
import ru.vasmarfas.cinewave.data.db.entity.InterestModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.FavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ViewedListModel

@Database(entities = [CollectionsModel::class, AuthTokenModel::class, FilmPersonInfoModel::class, ViewedListModel::class, FilmFiltersModel::class, ToWatchListModel::class, FavouriteListModel::class, InterestModel::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

}
