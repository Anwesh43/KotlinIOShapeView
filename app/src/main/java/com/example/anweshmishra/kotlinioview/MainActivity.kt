package com.example.anweshmishra.kotlinioview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.ioview.IOView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IOView.create(this)
    }
}
