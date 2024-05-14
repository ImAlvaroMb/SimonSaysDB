package com.example.simonssaysmemory

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
/*
class GameHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_history)

        val dbHelper = GameDatabaseHelper(context = this)
        val gameScores = dbHelper.getAllGameScores()

        val listView: ListView = findViewById(R.id.listView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gameScores)
        listView.adapter = adapter
    }
}*/