package com.example.nckh

import android.os.Bundle
import android.os.PersistableBundle
import android.sms.ezviewbinding.viewBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.nckh.databinding.SolveActivityBinding

class DetailActivity : AppCompatActivity() {

    private val binding by viewBinding(SolveActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}