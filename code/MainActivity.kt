package com.example.vsosh

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.InputEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.client.android.Intents.Scan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.io.File
import java.io.FileOutputStream
import kotlinx.serialization.*
import kotlinx.serialization.json.*


class MainActivity : AppCompatActivity() {
    private lateinit var scanBtn: Button
    private lateinit var historyBtn: Button
    private lateinit var createBtn: Button
    private lateinit var settingsBtn: ImageButton
    private lateinit var appName: TextView
    private lateinit var mainScreen: ConstraintLayout

    val ruWord = listOf("Создать QR-код", "Сканировать QR-код", "История сканирования")
    val enWord = listOf("Generate QR-Code", "Scan QR-Code", "View history")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanBtn = findViewById(R.id.scanBtn)
        createBtn = findViewById(R.id.createBtn)
        settingsBtn = findViewById(R.id.settingsBtn)
        appName = findViewById(R.id.appName)
        mainScreen = findViewById(R.id.main)
        historyBtn = findViewById(R.id.historyBtn)

        //При первом запуске приложения
        createAppDirectory() // Создание директории приложения
        createAppSettings() // Создать файл с настройсками приложения
        createAppLanguage() // Создать файл с языком интерфейса приложения
        createAppHistory() // Создать файл с историей сканирований
        crateAppAgreement()
        // setTheme() // Установить тему приложения в соответствии с настройками приложения
         setLanguage() // Установить язык интерфейса приложения в соответствии с настройками приложения

        crateAppAgreement()

        registerUiListener() //Основной цикл приложения
    }

    override fun onResume() {
        super.onResume()
        // setTheme()
         setLanguage()
        checkAgreement()
    }

    private fun registerUiListener() {
        scanBtn.setOnClickListener {
            scannerLauncher.launch(
                ScanOptions().setPrompt("Scan QR-Code")
                    .setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            )
        }

        createBtn.setOnClickListener {
            val createQRIntent = Intent(this, GeneratoCode_Activity::class.java)
            startActivity(createQRIntent)
        }

        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, Settings_Activity::class.java)
            startActivity(settingsIntent)
        }
        historyBtn.setOnClickListener {
            val historyIntent = Intent(this, HistoryActivity::class.java)
            startActivity(historyIntent)
        }

    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, LinkHandling_Activity::class.java)
            intent.putExtra("sent_data", result.contents)
            startActivity(intent)
        }

    }

    private fun createAppDirectory() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val f: File = File(folder, "QR_Scanner")
        if (!f.exists()) {
            f.mkdir()
        }
    }

    private fun crateAppAgreement() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "agreement.txt")
        if (!f.exists()) {
            f.writeText("no")
            askAgreement()
        }
    }

    private fun createAppSettings() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "theme.txt")
        if (!f.exists()) {
            f.writeText("light")
        }
    }

    private fun askAgreement() {
        val dialog = AlertDialog.Builder(this)
        val activity = MainActivity()

        val message = SpannableString("Подтверждаете ли вы, что ознакомились с содержанием Пользовательского Соглашения")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://46.17.248.105:2033/documentation"))
                startActivity(browserIntent)
            }
        }
        val option = DialogInterface.OnClickListener { option, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    option.dismiss()
                    val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
                    val f: File = File(folder, "agreement.txt")
                    if (f.exists()) {
                        f.writeText("yes")
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    activity.finish()
                    System.exit(0)
                }
            }
        }
        message.setSpan(clickableSpan, 0, message.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        dialog.setMessage(message)
        dialog.setPositiveButton("Да", option)
        dialog.setNegativeButton("Нет", option)

        val alertDialog = dialog.create()
        alertDialog.show()

        (alertDialog.findViewById<TextView>(android.R.id.message))?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun createAppLanguage() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "language.txt")
        if (!f.exists()) {
            f.writeText("eng")
        }
    }

    private fun createAppHistory() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "history.txt")
        if (!f.exists()) {
            f.createNewFile()
        }
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

    private fun checkAgreement() {
        val folder: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val f: File = File(folder, "agreement.txt")
        if (f.exists()) {
            if (f.readText().toString() == "no") {
                askAgreement()
            }
        }
        else {
            f.writeText("no")
        }
    }

    private fun setLight() {
        scanBtn.setBackgroundColor(Color.rgb(102, 79, 163))
        createBtn.setBackgroundColor(Color.rgb(102, 79, 163))
        historyBtn.setBackgroundColor(Color.rgb(102, 79, 163))
        appName.setTextColor(Color.BLACK)
        mainScreen.setBackgroundColor(Color.WHITE)
    }

    private fun setDark() {
        scanBtn.setBackgroundColor(Color.rgb(63, 49, 100))
        createBtn.setBackgroundColor(Color.rgb(63, 49, 100))
        historyBtn.setBackgroundColor(Color.rgb(63, 49, 100))
        appName.setTextColor(Color.WHITE)
        mainScreen.setBackgroundColor(Color.rgb(30, 31, 34))
    }

    private fun setEnglish() {
        scanBtn.text = enWord[1]
        createBtn.text = enWord[0]
        historyBtn.text = enWord[2]
    }

    private fun setRussian() {
        scanBtn.text = ruWord[1]
        createBtn.text = ruWord[0]
        historyBtn.text = ruWord[2]
    }

}