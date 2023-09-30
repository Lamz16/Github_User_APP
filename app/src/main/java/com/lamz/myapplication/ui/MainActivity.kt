package com.lamz.myapplication.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamz.myapplication.R
import com.lamz.myapplication.adapter.ListProfileAdapter
import com.lamz.myapplication.databinding.ActivityMainBinding
import com.lamz.myapplication.model.MainViewModel
import com.lamz.myapplication.model.SettingViewModel
import com.lamz.myapplication.response.ItemsItem
import com.lamz.myapplication.setting.SettingPreferences
import com.lamz.myapplication.setting.dataStore
import com.lamz.myapplication.ui.setting.SettingViewModelFactory


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
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

        /*Get Data from API*/
        val mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainViewModel::class.java]
        val layoutManager = LinearLayoutManager(this)

        binding?.rvListUser?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding?.rvListUser?.addItemDecoration(itemDecoration)
        mainViewModel.listUser.observe(this) { listUser ->
            setUserData(listUser)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }


        /*binding searchView for set query to search user*/
        with(binding) {
            this?.searchBar?.apply {
                inflateMenu(R.menu.option_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu1 -> {
                            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        R.id.menu2 -> {
                            val intent = Intent(this@MainActivity, ThemeActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        else -> false
                    }
                }

                searchView.setupWithSearchBar(searchBar)
                searchView.editText.setOnEditorActionListener { textView, _, _ ->
                    searchBar.text = searchView.text
                    mainViewModel.findUser(binding?.searchView?.text.toString())
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    searchView.hide()
                    false
                }
            }

        }

    }


    /*Set list user from adapter to recycler view*/

    private fun setUserData(listUser: List<ItemsItem>) {
        val adapter = ListProfileAdapter()
        adapter.submitList(listUser)
        binding?.rvListUser?.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}