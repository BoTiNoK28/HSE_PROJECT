package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.BuildConfig
import com.example.myapplication.ButtonGroup
import com.example.myapplication.R
import com.example.myapplication.SharedViewModel
import kotlin.collections.mutableListOf
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.SettingsClass
import com.example.vsosh.LinkHandlingActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.net.URI
import java.net.URLEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BlankFragment1 : Fragment() {

    private lateinit var scanButton: Button
    private lateinit var scannerLauncher: ActivityResultLauncher<ScanOptions>

    private lateinit var guideText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank1, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scanButton = view.findViewById<AppCompatButton>(R.id.scanButton)
        guideText = view.findViewById<TextView>(R.id.guideText)

        checkLanguage()
        scannerLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(requireContext(), LinkHandlingActivity::class.java)
                intent.putExtra("sent_data", result.contents)
                startActivity(intent)
            }
        }

        registerUiListener()
    }

    override fun onResume() {
        super.onResume()
        checkLanguage()
    }


    private fun registerUiListener() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Отсканируйте QR-код или штрих-код")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)
        }
        scanButton.setOnClickListener {
            scannerLauncher.launch(options)
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
                    scanButton.setText("Scan")
                    guideText.setText("Press the button to start scanning")
                }
                else if (settings.lang == "rus") {
                    scanButton.setText("Сканировать")
                    guideText.setText("Нажите, чтобы начать сканирование")
                }
            }
        } catch (e: Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment1().apply { // Исправлено на BlankFragment1
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}