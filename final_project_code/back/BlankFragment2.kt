package com.example.myapplication.fragments

import android.annotation.SuppressLint
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.SettingsClass
import com.google.gson.Gson
import com.google.zxing.Writer
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment2 : Fragment() {

    private lateinit var generate: AppCompatButton
    private lateinit var pattetn_colorBtn: AppCompatButton
    private lateinit var background_colorBtn: AppCompatButton
    private lateinit var saveBtn: AppCompatButton
    private lateinit var error_message: TextView
    private lateinit var bitMapQrCode: Bitmap
    private lateinit var mainScreen: ConstraintLayout
    private lateinit var color1: TextView
    private lateinit var color2: TextView

    private lateinit var imageView: ImageView

    private lateinit var editText: TextView

    var pDefaultColor = Color.BLACK
    var bDefaultColor = Color.WHITE

    val ruWord = listOf("Цвет Узора", "Цвет Фона", "Создать QR-код", "Сохранить")
    val enWord = listOf("Pattern", "Background", "Generate QR-Code", "Save")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank2, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generate = view.findViewById(R.id.generate)
        error_message = view.findViewById(R.id.error_message)
        pattetn_colorBtn = view.findViewById(R.id.pattetn_colorBtn)
        background_colorBtn = view.findViewById(R.id.background_colorBtn)
        saveBtn = view.findViewById(R.id.saveBtn)
        mainScreen = view.findViewById(R.id.main)
        color1 = view.findViewById(R.id.color1)
        color2 = view.findViewById(R.id.color2)
        editText = view.findViewById(R.id.editText)
        imageView = view.findViewById(R.id.imageView)

        registerUiListener()
        checkLanguage()
    }

    override fun onResume() {
        super.onResume()
        checkLanguage()
    }

    private fun registerUiListener() {
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
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(input, BarcodeFormat.QR_CODE, 550, 550, mapOf(EncodeHintType.CHARACTER_SET to "UTF-8"))
        bitMapQrCode = convertToBitmap(bitMatrix)
        imageView.setImageBitmap(convertToBitmap(bitMatrix))
        error_message.visibility = View.INVISIBLE
    }

    private fun openColorPickerDialog(button_id: Int) {
        if (button_id == 1) {
            var colorPickerDialog = AmbilWarnaDialog(requireContext(), pDefaultColor, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    pDefaultColor = color
                    setButtonColorSimple1(color)
                }
            })
            colorPickerDialog.show()
        }
        else {
            var colorPickerDialog = AmbilWarnaDialog(requireContext(), bDefaultColor, object : OnAmbilWarnaListener{
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    bDefaultColor = color
                    setButtonColorSimple2(color)
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
        val downloads: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val random: Int = (1000000..2000000).random()
        val fileName: String = "$random" + "_image.png"
        val file: File = File(downloads, fileName)
        try {
            val out: FileOutputStream = FileOutputStream(file)
            bitMapCode.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(requireContext(), "Image saved to App Folder", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Toast.makeText(requireContext(), "No QR-code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLanguage() {
        try {
            val f: File = File(requireContext().filesDir, "settings.json")
            val jsonString = f.readText()
            val settings = Gson().fromJson(jsonString, SettingsClass::class.java)
            if (f.exists()) {
                val fileContent = f.readText()
                if (settings.lang == "eng") {
                    changeLanguageToEn()
                }
                else if (settings.lang == "rus") {
                    changeLanguageToRu()
                }
            }
        } catch (e: Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
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

    private fun setButtonColorSimple1(color: Int) {
        val background = pattetn_colorBtn.background as? RippleDrawable

        background?.let { ripple ->
            // Получаем фон кнопки (второй элемент в ripple)
            val drawable = ripple.getDrawable(1) as? GradientDrawable
            drawable?.setColor(color)
        }
    }

    private fun setButtonColorSimple2(color: Int) {
        val background = background_colorBtn.background as? RippleDrawable

        background?.let { ripple ->
            // Получаем фон кнопки (второй элемент в ripple)
            val drawable = ripple.getDrawable(1) as? GradientDrawable
            drawable?.setColor(color)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}