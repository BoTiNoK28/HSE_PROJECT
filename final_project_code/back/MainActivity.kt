package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.fragments.BlankFragment1
import com.example.myapplication.fragments.BlankFragment2
import com.example.myapplication.fragments.BlankFragment3
import com.example.myapplication.fragments.BlankFragment4
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onStart() {
        super.onStart()
        createHistory()
        createSettings()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)

        val adapter = MyViewPagerAdapter(this)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Scan"
                1 -> "Create"
                2 -> "History"
                3 -> "Settings"
                else -> "Страница $position"
            }
        }.attach()
    }

    private fun createHistory() {
        val configFile: File = File(filesDir, "history.json")
        if (!configFile.exists()) {
            try {
                openFileOutput("history.json", Context.MODE_PRIVATE).use { outputStream ->
                    outputStream.write("".toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createSettings() {
        val gson: Gson = Gson()
        val json: String = gson.toJson(SettingsClass())
        val settingsFile: File = File(filesDir, "settings.json")
        if (!settingsFile.exists()) {
            try {
                openFileOutput("settings.json", Context.MODE_PRIVATE).use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class MyViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BlankFragment1() //Scan
                1 -> BlankFragment2() //Generate
                2 -> BlankFragment3() //History
                3 -> BlankFragment4() //Settings
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}