package com.lamz.myapplication.ui


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lamz.myapplication.R
import com.lamz.myapplication.adapter.SectionsPagerAdapter
import com.lamz.myapplication.database.FavoriteUser
import com.lamz.myapplication.databinding.ActivityDetailBinding
import com.lamz.myapplication.model.DetailViewModel
import com.lamz.myapplication.model.FavoriteViewModel
import com.lamz.myapplication.model.SettingViewModel
import com.lamz.myapplication.response.DetailUserResponse
import com.lamz.myapplication.setting.SettingPreferences
import com.lamz.myapplication.setting.dataStore
import com.lamz.myapplication.ui.setting.SettingViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = intent.getStringExtra(EXTRA_LOGIN).toString()
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

        val login = intent.getStringExtra(EXTRA_LOGIN)

        if (login != null) {
            detailViewModel.getProfile(login)
            detailViewModel.getFollowers(login)

        }

        detailViewModel.userProfile.observe(this) { detailUser ->
            setProfileData(detailUser)

            favoriteViewModel.getByUsername(detailUser.login).observe(this) { username ->

                with(binding) {
                    if (username != null){
                        favAdd?.setImageResource(R.drawable.ic_favorited)
                    }else{
                        favAdd?.setImageResource(R.drawable.ic_favorite)
                    }

                    favAdd?.setOnClickListener {

                        if (username != null) {
                            favoriteViewModel.delete(username)
                            favAdd.setImageDrawable(
                                ContextCompat.getDrawable(
                                    favAdd.context,
                                    R.drawable.ic_favorite
                                )
                            )
                            showToast("Berhasil menghapus ${username.username} dari favorite")
                        } else {
                            favoriteViewModel.insert(
                                FavoriteUser(
                                    detailUser.login,
                                    detailUser.avatarUrl
                                )
                            )
                            favAdd.setImageDrawable(
                                ContextCompat.getDrawable(
                                    favAdd.context,
                                    R.drawable.ic_favorited
                                )
                            )
                            showToast("Berhasil menambah ${detailUser.login} ke favorite")
                        }
                    }
                }
            }

        }

        detailViewModel.followLoading.observe(this) {
            showLoading(it)
        }
        favoriteViewModel = obtainViewModel(this@DetailActivity)


    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData(login: DetailUserResponse) {
        binding.tvUsername.text = login.login
        binding.tvFullName.text = login.name
        binding.tvFollower.text = resources.getString(R.string.followers, login.followers)
        binding.tvFollowing.text = resources.getString(R.string.followingAdd, login.following)
        Glide.with(this).load(login.avatarUrl)
            .centerCrop()
            .into(binding.imgProfile)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_LOGIN = "extra_login"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1, R.string.tab_text_2
        )
    }
}