package com.mcdane.box

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BoxActivity : AppCompatActivity() {
    private lateinit var view: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = GameView(
            this,
            intent.getStringExtra("playerName") ?: "Unnamed Player",
            intent.getIntExtra("maxLevel", 0),
        )
        setContentView(view)
    }
}