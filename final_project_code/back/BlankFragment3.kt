package com.example.myapplication.fragments

import ButtonAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.myapplication.ButtonGroup
import com.example.myapplication.R
import com.example.myapplication.SettingsClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment3 : Fragment() {

    private lateinit var NotificationTextView: TextView
    private lateinit var listView: ListView
    private lateinit var clearButton: ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationTextView = view.findViewById(R.id.NotificationTextView)
        listView = view.findViewById(R.id.listView)
        clearButton = view.findViewById(R.id.clearButton)
        checkLanguage()
        checkHistory()

        clearButton.setOnClickListener {
            clearList()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val f: File = File(requireContext().filesDir, "history.json")
            val jsonString = f.readText()
            val listType = object : TypeToken<List<ButtonGroup>>() {}.type
            var list = Gson().fromJson<List<ButtonGroup>>(jsonString, listType) ?: emptyList()

            val selectedItem: String = list[position].link
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip: ClipData = ClipData.newPlainText("text", selectedItem)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank3, container, false)
    }

    override fun onResume() {
        super.onResume()
        checkHistory()
        checkLanguage()
    }

    private fun checkHistory() {
        val f: File = File(requireContext().filesDir, "history.json")
        val jsonString = f.readText()
        val listType = object : TypeToken<List<ButtonGroup>>() {}.type
        var list = Gson().fromJson<List<ButtonGroup>>(jsonString, listType) ?: emptyList()
        if (list.size > 0) {
            NotificationTextView.visibility = View.INVISIBLE
            updateList(list)
        }
        else {
            NotificationTextView.visibility = View.VISIBLE
        }
    }

    private fun clearList() {
        val f: File = File(requireContext().filesDir, "history.json")
        f.writeText("")
        val jsonString = f.readText()
        val listType = object : TypeToken<List<ButtonGroup>>() {}.type
        var list = Gson().fromJson<List<ButtonGroup>>(jsonString, listType) ?: emptyList()
        updateList(list)
        NotificationTextView.visibility = View.VISIBLE
    }

    private fun updateList(list: List<ButtonGroup>) {
        val adapter = ButtonAdapter(requireContext(), list)
        listView.adapter = adapter
        adapter.updateData(list)
    }

    private fun checkLanguage() {
        try {
            val f: File = File(requireContext().filesDir, "settings.json")
            val jsonString = f.readText()
            val settings = Gson().fromJson(jsonString, SettingsClass::class.java)
            if (f.exists()) {
                val fileContent = f.readText()
                if (settings.lang == "eng") {
                    NotificationTextView.setText("History is empty")
                }
                else if (settings.lang == "rus") {
                    NotificationTextView.setText("История пуста")
                }
            }
        } catch (e: Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
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
