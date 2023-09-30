package com.lamz.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamz.myapplication.adapter.ListFavoriteAdapter
import com.lamz.myapplication.databinding.ActivityFavoriteBinding
import com.lamz.myapplication.model.FavoriteViewModel
import com.lamz.myapplication.model.SettingViewModel
import com.lamz.myapplication.setting.SettingPreferences
import com.lamz.myapplication.setting.dataStore
import com.lamz.myapplication.ui.setting.SettingViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private var _favoriteBinding: ActivityFavoriteBinding? = null
    private val binding get() = _favoriteBinding
    private lateinit var userAdapter: ListFavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _favoriteBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        userAdapter = ListFavoriteAdapter()

        val favoriteViewModel = obtainViewModel(this@FavoriteActivity)
        favoriteViewModel.getAllFavorites().observe(this) { favoriteList ->
            userAdapter.submitList(favoriteList)
        }
        binding?.rvListFavorite?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userAdapter
        }

        binding?.btnBack?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(FavoriteViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        _favoriteBinding = null
    }
}