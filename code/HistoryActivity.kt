package com.example.vsosh

import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.util.HashMap

class HistoryActivity : AppCompatActivity() {

    private lateinit var sideUp: MaterialToolbar
    private lateinit var textView: TextView
    private lateinit var listView: ListView
    private lateinit var clearBtn: Button
    private lateinit var screen: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        sideUp = findViewById(R.id.sideUp2)
        textView = findViewById(R.id.textView)
        listView = findViewById(R.id.listView)
        clearBtn = findViewById(R.id.clearBtn)
        screen = findViewById(R.id.main)

        setTheme()

        val links = readAppLinks()
        val stats = readAppLinkStats()

        val list = ArrayList<HashMap<String, String>>()

        for (i in links.indices) {
            val map = HashMap<String, String>()
            map["linkName"] = (i+1).toString() + ") " + links[i]
            map["linkState"] = stats[i]

            list.add(map)
        }

        val from = arrayOf("linkName", "linkState")
        val to = intArrayOf(R.id.text1, R.id.text2)

        val simpleAdapter = SimpleAdapter(this, list, R.layout.list_item, from, to)

        listView.adapter = simpleAdapter

        registerUiListener()
    }

    override fun onResume() {
        super.onResume()
        setTheme()
//        setLanguage()
    }

    private fun registerUiListener() {
        clearBtn.setOnClickListener {
            val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
            val f: File = File(folder, "history.txt")
            f.writeText("")

            val links = readAppLinks()
            val stats = readAppLinkStats()

            val list = ArrayList<HashMap<String, String>>()

            for (i in links.indices) {
                val map = HashMap<String, String>()
                map["linkName"] = (i+1).toString() + ") " + links[i]
                map["linkState"] = stats[i]

                list.add(map)
            }

            val from = arrayOf("linkName", "linkState")
            val to = intArrayOf(R.id.text1, R.id.text2)

            val simpleAdapter = SimpleAdapter(this, list, R.layout.list_item, from, to)

            listView.adapter = simpleAdapter
        }
    }

    private fun readAppLinks(): List<String> {
        val linkList = ArrayList<String>()
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "history.txt")

        for (i in f.readLines(Charsets.UTF_8).indices) {
            if (i % 2 == 0) {
                linkList.add(f.readLines(Charsets.UTF_8)[i])
            }
        }
        return linkList
    }

    private fun readAppLinkStats(): List<String> {
        val linkStatList = ArrayList<String>()
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "history.txt")

        for (i in f.readLines(Charsets.UTF_8).indices) {
            if (i % 2 != 0) {
                linkStatList.add(f.readLines(Charsets.UTF_8)[i])
            }
        }
        return linkStatList
    }

    private fun setTheme() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "theme.txt")
        if (f.exists()) {
            val fileContent = f.readText()
            if (fileContent == "light") {
                setLight()
            }
            else if (fileContent == "dark") {
                setDark()
            }
        }
    }

    private fun setLight() {
        screen.setBackgroundColor(Color.WHITE)
        sideUp.setBackgroundColor(Color.rgb(102, 79, 163))
        clearBtn.setBackgroundColor(Color.rgb(102, 79, 163))
    }

    private fun setDark() {
        screen.setBackgroundColor(Color.rgb(30, 31, 34))
        clearBtn.setBackgroundColor(Color.rgb(63, 49, 100))
        sideUp.setBackgroundColor(Color.rgb(63, 49, 100))
    }
}