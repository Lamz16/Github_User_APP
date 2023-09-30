package com.lamz.myapplication.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lamz.myapplication.database.FavoriteRepository
import com.lamz.myapplication.database.FavoriteUser

class FavoriteViewModel(application: Application): ViewModel() {
    private val mFavoriteRepository : FavoriteRepository = FavoriteRepository(application)

    fun getAllFavorites(): LiveData<List<FavoriteUser>> = mFavoriteRepository.getAllFavorite()

    fun insert(favorite: FavoriteUser) {
        mFavoriteRepository.insert(favorite)
    }

    fun delete(favorite: FavoriteUser) {
        mFavoriteRepository.delete(favorite)
    }

    fun getByUsername(username: String): LiveData<FavoriteUser> {
        return mFavoriteRepository.getFavoriteUserByusername(username)
    }

}