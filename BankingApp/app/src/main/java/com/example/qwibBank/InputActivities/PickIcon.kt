package com.example.qwibBank.InputActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qwibBank.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.GridLayoutManager
import com.example.qwibBank.RecyclerAdaptors.ImageAdapter


class PickIcon: AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.getSupportActionBar()?.hide()

        setContentView(R.layout.select_icons)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ImageAdapter(this)
        recyclerView.layoutManager = GridLayoutManager(this, 5)
        recyclerView.adapter = adapter
        val type = intent.getStringExtra("type")
        adapter.setImages(type!!)

        adapter.setOnItemClickListener {
            val replyIntent = Intent()
            replyIntent.putExtra("icon", it)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
        val fabBack = findViewById<FloatingActionButton>(R.id.back)
        fabBack.setOnClickListener {
            finish()
        }
    }
}