package com.example.vsosh

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.Writer
import com.journeyapps.barcodescanner.ScanOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.random.Random

class GeneratoCode_Activity : AppCompatActivity() {

    private lateinit var generate: AppCompatButton
    private lateinit var pattetn_colorBtn: AppCompatButton
    private lateinit var background_colorBtn: AppCompatButton
    private lateinit var saveBtn: AppCompatButton
    private lateinit var error_message: TextView
    private lateinit var bitMapQrCode: Bitmap
    private lateinit var mainScreen: ConstraintLayout
    private lateinit var color1: TextView
    private lateinit var color2: TextView

    var pDefaultColor = Color.BLACK
    var bDefaultColor = Color.WHITE

    val ruWord = listOf("Цвет Узора", "Цвет Фона", "Создать QR-код", "Сохранить")
    val enWord = listOf("Pattern", "Background", "Generate QR-Code", "Save")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generato_code)

        generate = findViewById(R.id.generate)
        error_message = findViewById(R.id.error_message)
        pattetn_colorBtn = findViewById(R.id.pattetn_colorBtn)
        background_colorBtn = findViewById(R.id.background_colorBtn)
        saveBtn = findViewById(R.id.saveBtn)
        mainScreen = findViewById(R.id.main)
        color1 = findViewById(R.id.color1)
        color2 = findViewById(R.id.color2)
        // setTheme()
        registerUiListener()
    }

    override fun onResume() {
        super.onResume()
//        setTheme()
        setLanguage()
    }

    private fun registerUiListener() {
        val editText: TextView = findViewById(R.id.editTextText)
        generate.setOnClickListener {
            val enteredData = editText.text.toString()
            if (enteredData == "") {
                error_message.setTextColor(Color.RED)
                error_message.visibility = View.VISIBLE
            }
            else {
                generateQR(enteredData)
            }
        }

        pattetn_colorBtn.setOnClickListener {
            openColorPickerDialog(1)
        }
        background_colorBtn.setOnClickListener {
            openColorPickerDialog(2)
        }
        saveBtn.setOnClickListener {
            saveQRCode(bitMapQrCode)
        }
    }

    private fun generateQR(input:String) {
        val hints = Hashtable<String, String>()
        hints.put(EncodeHintType.CHARACTER_SET.toString(), "UTF-8")

        val imageView: ImageView = findViewById(R.id.imageView)
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(input, BarcodeFormat.QR_CODE, 550, 550, mapOf(EncodeHintType.CHARACTER_SET to "UTF-8"))
        bitMapQrCode = convertToBitmap(bitMatrix)
        imageView.setImageBitmap(convertToBitmap(bitMatrix))
        error_message.visibility = View.INVISIBLE
    }

    private fun openColorPickerDialog(button_id: Int) {
        if (button_id == 1) {
            var colorPickerDialog = AmbilWarnaDialog(this, pDefaultColor, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    pDefaultColor = color
                    // TODO: нормальная смена цвета
                }
            })
            colorPickerDialog.show()
        }
        else {
            var colorPickerDialog = AmbilWarnaDialog(this, bDefaultColor, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    bDefaultColor = color
                    background_colorBtn.setBackgroundColor(bDefaultColor)
                }
            })
            colorPickerDialog.show()
        }
    }

    private fun convertToBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (bitMatrix.get(x, y)) {
                    bitmap.setPixel(x, y, pDefaultColor)
                } else {
                    bitmap.setPixel(x, y, bDefaultColor)
                }
            }
        }

        return bitmap
    }

    private fun saveQRCode(bitMapCode: Bitmap) {
        val downloads: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/QR_Scanner"
        val random: Int = (1000000..2000000).random()
        val fileName: String = "$random" + "_image.png"
        val file: File = File(downloads, fileName)
        try {
            val out: FileOutputStream = FileOutputStream(file)
            bitMapCode.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Image saved to App Folder", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Toast.makeText(this, "No QR-Code", Toast.LENGTH_SHORT).show()
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
                changeLanguageToEn()
            }
            else if (fileContent == "rus") {
                changeLanguageToRu()
            }
        }
    }

    private fun setLight() {
        generate.setBackgroundColor(Color.rgb(102, 79, 163))
        saveBtn.setBackgroundColor(Color.rgb(102, 79, 163))
        color1.setTextColor(Color.BLACK)
        color2.setTextColor(Color.BLACK)
        val editText: TextView = findViewById(R.id.editTextText)
        editText.setTextColor(Color.BLACK)
        mainScreen.setBackgroundColor(Color.WHITE)
    }

    private fun setDark() {
        generate.setBackgroundColor(Color.rgb(63, 49, 100))
        saveBtn.setBackgroundColor(Color.rgb(63, 49, 100))
        color1.setTextColor(Color.WHITE)
        color2.setTextColor(Color.WHITE)
        val editText: TextView = findViewById(R.id.editTextText)
        editText.setTextColor(Color.WHITE)
        mainScreen.setBackgroundColor(Color.rgb(30, 31, 34))
    }

    private fun changeLanguageToEn() {
        color1.text = enWord[0]
        color2.text = enWord[1]
        generate.text = enWord[2]
        saveBtn.text = enWord[3]
    }

    private fun changeLanguageToRu() {
        color1.text = ruWord[0]
        color2.text = ruWord[1]
        generate.text = ruWord[2]
        saveBtn.text = ruWord[3]
    }
}