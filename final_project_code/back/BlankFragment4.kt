package com.example.myapplication.fragments

import ButtonAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioButton
import android.widget.SimpleAdapter
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.ButtonGroup
import com.example.myapplication.R
import com.example.myapplication.SettingsClass
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment4 : Fragment() {

    private lateinit var test: Switch
    private lateinit var textView2: TextView
    private lateinit var textView4: TextView
    private lateinit var screen: ConstraintLayout
    private lateinit var ruLanguageButton: RadioButton
    private lateinit var enLanguageButton: RadioButton
    private lateinit var guideButton: Button

    val ruWord = listOf("Изменить тему приложения: Светлая/Темная", "Изменить язык интерфейса: Рус/Анг", "Русский", "Английский", "Руководство Пользователя")
    val enWord = listOf("Change app color theme: Light/Dark", "Change app language: Rus/Eng", "Russian", "English", "User Guide")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test = view.findViewById(R.id.test)
        textView2 = view.findViewById(R.id.textView2)
        textView4 = view.findViewById(R.id.textView4)
        screen = view.findViewById(R.id.main)
        ruLanguageButton = view.findViewById(R.id.russianRadio)
        enLanguageButton = view.findViewById(R.id.englishRadio)
        guideButton = view.findViewById(R.id.guideButton)

        registerUiListener()
        setLanguage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank4, container, false)
    }

//    override fun onResume() {
//        super.onResume()
//        setLanguage()
//    }

    private fun registerUiListener() {
        /*test.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeThemeFile("dark")
                setDarkTheme()
            }
            else {
                changeThemeFile("light")
                setLightheme()
            }
        }*/

        ruLanguageButton.setOnClickListener {
            changeLanguageFile("rus")
            setLanguage()
        }

        enLanguageButton.setOnClickListener {
            changeLanguageFile("eng")
            setLanguage()
        }

        guideButton.setOnClickListener {
            val link: String = "http://server_ip/"
            var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }
    }

    private fun setLanguage() {
        try {
            val f: File = File(requireContext().filesDir, "settings.json")
            val jsonString = f.readText()
            val settings = Gson().fromJson(jsonString, SettingsClass::class.java)
            if (f.exists()) {
                val fileContent = f.readText()
                if (settings.lang == "eng") {
                    changeLanguageToEn()
                    enLanguageButton.isChecked = true
                    ruLanguageButton.isChecked = false
                } else if (settings.lang == "rus") {
                    changeLanguageToRu()
                    ruLanguageButton.isChecked = true
                    enLanguageButton.isChecked = false
                }
            }
        } catch (e: Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeLanguageFile(lang: String) {
        val f: File = File(requireContext().filesDir, "settings.json")
        val jsonString = f.readText()
        val settings = Gson().fromJson(jsonString, SettingsClass::class.java)
        settings.lang = lang
        f.writeText(Gson().toJson(settings))

    }

    private fun changeLanguageToRu() {
        textView2.text = ruWord[0]
        textView4.text = ruWord[1]
        ruLanguageButton.text = ruWord[2]
        enLanguageButton.text = ruWord[3]
        guideButton.text = ruWord[4]
    }

    private fun changeLanguageToEn() {
        textView2.text = enWord[0]
        textView4.text = enWord[1]
        ruLanguageButton.text = enWord[2]
        enLanguageButton.text = enWord[3]
        guideButton.text = enWord[4]
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
