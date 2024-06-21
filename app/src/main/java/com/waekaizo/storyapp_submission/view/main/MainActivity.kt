package com.waekaizo.storyapp_submission.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.waekaizo.storyapp_submission.R
import com.waekaizo.storyapp_submission.data.ResultState
import com.waekaizo.storyapp_submission.data.api.ListStoryItem
import com.waekaizo.storyapp_submission.databinding.ActivityMainBinding
import com.waekaizo.storyapp_submission.view.addstory.AddStory
import com.waekaizo.storyapp_submission.view.LoginViewModelFactory
import com.waekaizo.storyapp_submission.view.maps.MapsActivity
import com.waekaizo.storyapp_submission.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        LoginViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        mainViewModel.getSession().observe(this) { data ->
            if (!data.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setAllStories()
            }
        }

        setupActionLogout()
        setupActionAdd()

    }

    private fun setupActionAdd() {
        binding.fabAddStory.setOnClickListener{
            val intent = Intent(this, AddStory::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionLogout() {

        binding.toAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    mainViewModel.logout()
                    true
                }
                R.id.menu2 -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setAllStories() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.storyPager.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}