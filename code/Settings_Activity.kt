package com.example.vsosh

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.InspectableProperty
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlin.properties.Delegates
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class Settings_Activity : AppCompatActivity() {

    private lateinit var test: Switch
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var screen: ConstraintLayout
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var ruLanguageButton: RadioButton
    private lateinit var enLanguageButton: RadioButton
    private lateinit var guideButton: Button

    val ruWord = listOf("Настройки", "Изменить тему приложения: Светлая/Темная", "Изменить язык интерфейса: Рус/Анг", "Английский", "Русский", "Руководство Пользователя")
    val enWord = listOf("Settings", "Change app color theme: Light/Dark", "Change app language: Rus/Eng", "English", "Russian", "User Guide")


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        test = findViewById(R.id.test)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)
        screen = findViewById(R.id.main)
        materialToolbar = findViewById(R.id.sideUp)
        ruLanguageButton = findViewById(R.id.russianRadio)
        enLanguageButton = findViewById(R.id.englishRadio)
        guideButton = findViewById(R.id.guideButton)

//        setTheme()
        setLanguage()
        registerUiListener()
    }

    override fun onResume() {
        super.onResume()
//        setTheme()
        setLanguage()
    }

    private fun registerUiListener() {
        test.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeThemeFile("dark")
                setDarkTheme()
            }
            else {
                changeThemeFile("light")
                setLightheme()
            }
        }

        ruLanguageButton.setOnClickListener {
            changeLanguageFile("rus")
            setLanguage()
            enLanguageButton.isChecked = false
        }

        enLanguageButton.setOnClickListener {
            changeLanguageFile("eng")
            setLanguage()
            ruLanguageButton.isChecked = false
        }

        guideButton.setOnClickListener {
            val link: String = "http://46.17.248.105:2033/home"
            var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }
    }

    private fun setDarkTheme() {
        screen.setBackgroundColor(Color.rgb(30, 31, 34))
        textView2.setTextColor(Color.WHITE)
        textView4.setTextColor(Color.WHITE)
        ruLanguageButton.setTextColor(Color.WHITE)
        enLanguageButton.setTextColor(Color.WHITE)


        test.text = "Dark"
        test.setTextColor(Color.WHITE)

        materialToolbar.setBackgroundColor(Color.rgb(63, 49, 100))

    }

    private fun setLightheme() {
        screen.setBackgroundColor(Color.WHITE)
        textView2.setTextColor(Color.BLACK)
        textView4.setTextColor(Color.BLACK)
        ruLanguageButton.setTextColor(Color.BLACK)
        enLanguageButton.setTextColor(Color.BLACK)

        test.setTextColor(Color.BLACK)
        test.text = "Light"

        materialToolbar.setBackgroundColor(Color.rgb(102, 79, 163))

    }

    private fun changeThemeFile(mode: String) {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "theme.txt")
        f.writeText(mode)
    }

    private fun changeLanguageFile(mode: String) {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "language.txt")
        f.writeText(mode)
    }

    private fun setTheme() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "theme.txt")
        if (f.exists()) {
            val fileContent = f.readText()
            if (fileContent == "light") {
                setLightheme()
                test.isChecked = false
            }
            else if (fileContent == "dark") {
                setDarkTheme()
                test.isChecked = true
            }
        }
    }

    private fun setLanguage() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "language.txt")
        if (f.exists()) {
            val fileContent = f.readText()
            if (fileContent == "eng") {
                changeLanguageToEn()
                enLanguageButton.isChecked = true
            }
            else if (fileContent == "rus") {
                changeLanguageToRu()
                ruLanguageButton.isChecked = true
            }
        }
    }

    private fun changeLanguageToRu() {
        textView3.text = ruWord[0]
        textView2.text = ruWord[1]
        textView4.text = ruWord[2]
        ruLanguageButton.text = ruWord[4]
        enLanguageButton.text = ruWord[3]
        guideButton.text = ruWord[5]
    }

    private fun changeLanguageToEn() {
        textView3.text = enWord[0]
        textView2.text = enWord[1]
        textView4.text = enWord[2]
        ruLanguageButton.text = enWord[4]
        enLanguageButton.text = enWord[3]
        guideButton.text = enWord[5]
    }
}