package com.github.aakumykov.android_storage_lister_demo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.simple_storage_lister.SimpleStorageLister

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            listStorages()
        }

        listStorages()
    }

    private fun listStorages() {
        StringBuilder().apply {
            SimpleStorageLister(this@MainActivity).storageDirectories.forEach { storage: AndroidStorageDirectory? ->
                storage?.also {
                    append("\"${it.name}\" (${it.type}) ")
                    append("\n")
                    append(it.path)
                    append("\n")
                    append("\n")
                }
            }
        }.also {
            findViewById<TextView>(R.id.textView).text = it
        }
    }
}