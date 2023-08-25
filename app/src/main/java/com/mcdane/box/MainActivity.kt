package com.mcdane.box

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup

class MainActivity : AppCompatActivity() {
    private var maxLevel: Int = 0
    private lateinit var difficultyLevels: RadioGroup
    private lateinit var singleGameButton: Button
    private lateinit var pvpGameButton: Button
    private lateinit var leaderboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        difficultyLevels = findViewById(R.id.difficulty_levels)
        difficultyLevels.setOnCheckedChangeListener { _, checkId ->
           onDifficultyLevelChanged(checkId)
        }

        singleGameButton = findViewById(R.id.start_single_player_game_button)
        singleGameButton.setOnClickListener { onSingleGameClicked() }

        pvpGameButton = findViewById(R.id.start_pvp_button)
        pvpGameButton.setOnClickListener{ onPvPGameClicked() }

        leaderboardButton = findViewById(R.id.leaderboard_button)
        leaderboardButton.setOnClickListener{ onLeaderboardClicked() }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    private fun onDifficultyLevelChanged(checkId: Int) {
        when (checkId) {
            R.id.easy_button -> maxLevel = 0
            R.id.medium_button -> maxLevel = 1
            R.id.hard_button -> maxLevel = 2
        }
        Log.i(TAG, "maxLevel=$maxLevel")
    }

    private fun onSingleGameClicked() {
        val intent = Intent(this, BoxActivity::class.java)
        intent.putExtra("maxLevel", maxLevel)
        startActivity(intent)
    }

    private fun onPvPGameClicked() {
        Log.i(TAG, "PvP Game")
    }

    private fun onLeaderboardClicked() {
        Log.i(TAG, "Leaderboard")
    }
}