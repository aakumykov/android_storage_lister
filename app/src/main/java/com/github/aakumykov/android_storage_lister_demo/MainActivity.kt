package com.github.aakumykov.android_storage_lister_demo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.StorageDirectory

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
            StringBuilder().apply {
                DemoAndroidStorageLister(this@MainActivity).storageDirectories.forEach { storage: Storage? ->
                    storage?.also {
                        append("[")
                        append(it.type)
                        append("] ")
                        append(it.name)
                        append(": ")
                        append(it.path)
                        append("\n")
                    }
                }
            }.also {
                findViewById<TextView>(R.id.textView).text = it
            }
        }
    }
}