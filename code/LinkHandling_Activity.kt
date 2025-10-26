package com.example.vsosh

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import androidx.core.net.toUri

class LinkHandling_Activity : AppCompatActivity() {

    private lateinit var output_field: TextView
    private lateinit var apiOtput1: TextView
    private lateinit var apiOtput2: TextView
    private lateinit var apiOtput3: TextView
    private lateinit var scannedData: TextView
    private lateinit var Text: TextView
    private lateinit var safetyAnalysis: TextView
    private lateinit var google: TextView
    private lateinit var yandex: TextView
    private lateinit var abuse: TextView
    private lateinit var copy_button: Button
    private lateinit var browser_button: Button
    private lateinit var back_button: Button
    private lateinit var main: ConstraintLayout

    val ruWord = listOf("Результат Сканирования", "Текст", "Копировать", "Открыть в браузере", "Результат проверки", "Вывод:", "Назад в основное меню")
    val enWord = listOf("Scanned Data", "Text", "Copy", "Open in browser", "Safety Analysis", "Summary:", "Back to Main Menu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_handling)

        output_field = findViewById(R.id.output_field)
        copy_button = findViewById(R.id.copy_button)
        browser_button = findViewById(R.id.browser_button)
        back_button = findViewById(R.id.backBtn)
        apiOtput1 = findViewById(R.id.apiOutput1)
        apiOtput2 = findViewById(R.id.apiOutput2)
        apiOtput3 = findViewById(R.id.apiOutput3)
        scannedData = findViewById(R.id.scannedData)
        Text = findViewById(R.id.Text)
        safetyAnalysis = findViewById(R.id.safetyAnalysis)
        google = findViewById(R.id.google)
        yandex = findViewById(R.id.yandex)
        abuse = findViewById(R.id.abuse)
        main = findViewById(R.id.main)

//        setTheme()
        setLanguage()

        val intent: String? = getIntent().getStringExtra("sent_data")

        output_field.setText(intent)
        if (URLUtil.isValidUrl(intent)) {
            checkLink(intent, "yandex_api")
            checkLink(intent, "google_api")
            checkLink(intent, "abuse_api")
            setSummary(intent)
        }
        registerCopyButtonPress(intent)
    }

    private fun registerCopyButtonPress(text: String?) {
        copy_button.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip: ClipData = ClipData.newPlainText("text", text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(applicationContext, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        browser_button.setOnClickListener {
            val link: String = output_field.text.toString()
            val urlIntent = Intent(Intent.ACTION_VIEW, link.toUri())
            try {
                startActivity(urlIntent)
            } catch(e: Exception) {}
        }

        back_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun checkLink(link: String?, commandName: String) {
            val queue = Volley.newRequestQueue(this)
            val encodedLink = URLEncoder.encode(link, "UTF-8")
            val url = "http://46.17.248.105:2033/$commandName?link=$encodedLink"

            val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String>{ response ->
                    if (response.toString() == "safe") {
                        if (commandName == "yandex_api") {
                            apiOtput1.text = "SAFE"
                            apiOtput1.setTextColor(Color.GREEN)
                        }
                        else if (commandName == "abuse_api") {
                            apiOtput3.text = "SAFE"
                            apiOtput3.setTextColor(Color.GREEN)
                        }
                        else {
                            apiOtput2.text = "SAFE"
                            apiOtput2.setTextColor(Color.GREEN)
                        }
                    }
                    else {
                        if (commandName == "yandex_api") {
                            apiOtput1.text = "UNSAFE"
                            apiOtput1.setTextColor(Color.RED)
                        }
                        else if (commandName == "abuse_api") {
                            apiOtput3.text = "UNSAFE"
                            apiOtput3.setTextColor(Color.RED)
                        }
                        else {
                            apiOtput2.text = "UNSAFE"
                            apiOtput2.setTextColor(Color.RED)
                        }
                    }
                },
                Response. ErrorListener{ error -> apiOtput1.text = "ERROR" })
            queue.add(stringRequest)
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

    private fun setLanguage() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "language.txt")
        if (f.exists()) {
            val fileContent = f.readText()
            if (fileContent == "eng") {
                setEnglish()
            }
            else if (fileContent == "rus") {
                setRussian()
            }
        }
    }

    private fun setLight() {
        main.setBackgroundColor(Color.WHITE)
        copy_button.setBackgroundColor(Color.rgb(102, 79, 163))
        browser_button.setBackgroundColor(Color.rgb(102, 79, 163))
        browser_button.setBackgroundColor(Color.rgb(102, 79, 163))
        back_button.setBackgroundColor(Color.rgb(102, 79, 163))
        scannedData.setTextColor(Color.BLACK)
        Text.setTextColor(Color.BLACK)
        safetyAnalysis.setTextColor(Color.BLACK)
        yandex.setTextColor(Color.BLACK)
        google.setTextColor(Color.BLACK)
        abuse.setTextColor(Color.BLACK)
    }

    private fun setDark() {
        main.setBackgroundColor(Color.rgb(30, 31, 34))
        copy_button.setBackgroundColor(Color.rgb(63, 49, 100))
        browser_button.setBackgroundColor(Color.rgb(63, 49, 100))
        browser_button.setBackgroundColor(Color.rgb(63, 49, 100))
        back_button.setBackgroundColor(Color.rgb(63, 49, 100))
        scannedData.setTextColor(Color.WHITE)
        Text.setTextColor(Color.WHITE)
        safetyAnalysis.setTextColor(Color.WHITE)
        yandex.setTextColor(Color.WHITE)
        google.setTextColor(Color.WHITE)
        abuse.setTextColor(Color.WHITE)
    }

    private fun setRussian() {
        scannedData.text = ruWord[0]
        Text.text = ruWord[1]
        copy_button.text = ruWord[2]
        browser_button.text = ruWord[3]
        safetyAnalysis.text = ruWord[4]
        back_button.text = ruWord[6]
    }

    private fun setEnglish() {
        scannedData.text = enWord[0]
        Text.text = enWord[1]
        copy_button.text = enWord[2]
        browser_button.text = enWord[3]
        safetyAnalysis.text = enWord[4]
        back_button.text = enWord[6]
    }

    private fun setSummary(link: String?) {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "history.txt")
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        f.appendText(link + "\n" + currentDate + "\n")
    }
}