package com.lamz.myapplication.database

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val mFavoriteDao : FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = FavoriteUserDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }
    fun getAllFavorite(): LiveData<List<FavoriteUser>> = mFavoriteDao.getAllFavorites()

    fun insert(favorite : FavoriteUser) {
        executorService.execute { mFavoriteDao.insert(favorite) }
    }
    fun delete(favorite : FavoriteUser) {
        executorService.execute {mFavoriteDao.delete(favorite) }
    }

    fun getFavoriteUserByusername(username : String) : LiveData<FavoriteUser> {
            return  mFavoriteDao.getFavoriteUserByUsername(username)
    }
}