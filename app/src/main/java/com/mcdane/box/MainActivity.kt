package com.mcdane.box

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File

@Serializable
data class Player(val firstName: String, val lastName: String) {
    override fun toString(): String = "$firstName $lastName"
}

class MainActivity : Activity() {

    companion object {
        const val PLAYER_FILE = "player.json"
        const val PROFILE_REQUEST_CODE = 1
    }

    private var maxLevel: Int = 0
    private lateinit var playerNameText: TextView
    private lateinit var difficultyLevels: RadioGroup
    private lateinit var singleGameButton: Button
    private lateinit var pvpGameButton: Button
    private lateinit var profileButton: Button
    private lateinit var leaderboardButton: Button
    var player: Player? = null
        private set
    val playerName: String
        get() = player?.let { player.toString() } ?: "Unnamed Player"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlayer()
        initUI()
        initDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != PROFILE_REQUEST_CODE) {
            return
        }

        if (resultCode == ProfileActivity.RESULT_SAVE) {
            savePlayer(data!!)
        }
    }

    private fun initUI() {
        playerNameText = findViewById(R.id.player_name)
        playerNameText.text = playerName

        difficultyLevels = findViewById(R.id.difficulty_levels)
        difficultyLevels.setOnCheckedChangeListener { _, checkId ->
            onDifficultyLevelChanged(checkId)
        }

        singleGameButton = findViewById(R.id.start_single_player_game_button)
        singleGameButton.setOnClickListener { onSingleGameClicked() }

        pvpGameButton = findViewById(R.id.start_pvp_button)
        pvpGameButton.setOnClickListener{ onPvPGameClicked() }

        profileButton = findViewById(R.id.profile_button)
        profileButton.setOnClickListener{ onProfileClicked() }

        leaderboardButton = findViewById(R.id.high_scores_button)
        leaderboardButton.setOnClickListener{ onHighScoresClicked() }
    }

    private fun initPlayer() {
        val playerFile = File(filesDir, PLAYER_FILE)
        if (playerFile.exists()) {
            player = Json.decodeFromStream<Player>(playerFile.inputStream())
        }
    }

    private fun initDB() {
        BoxDatabase.init(this)
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
        intent.putExtra("playerName", playerName)
        intent.putExtra("isMultiplayer", false)
        startActivity(intent)
    }

    private fun onPvPGameClicked() {
        AlertDialog.Builder(this).apply{
            val view = layoutInflater.inflate(R.layout.server_or_client, null)
            setView(view)

            val serverOrClient = view.findViewById<RadioGroup>(R.id.server_or_client)

            setPositiveButton(R.string.ok) { dialog, _ ->
                startPvPGame(
                    serverOrClient.checkedRadioButtonId == R.id.run_as_server_button
                )
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun onProfileClicked() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("playerSet", player != null)
        player?.apply {
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
        }
        startActivityForResult(intent, PROFILE_REQUEST_CODE)
    }

    private fun onHighScoresClicked() {
        val intent = Intent(this, HighScoresActivity::class.java)
        startActivity(intent)
    }

    private fun startPvPGame(serverOrClient: Boolean) {
        val intent = Intent(this, BoxActivity::class.java)
        intent.putExtra("maxLevel", maxLevel)
        intent.putExtra("playerName", playerName)
        intent.putExtra("isMultiplayer", true)
        intent.putExtra("serverOrClient", serverOrClient)
        startActivity(intent)
    }

    private fun savePlayer(intent: Intent) {
        player = Player(
            intent.getStringExtra("firstName").toString(),
            intent.getStringExtra("lastName").toString()
        )
        playerNameText.text = playerName

        openFileOutput(PLAYER_FILE, Context.MODE_PRIVATE).use {
            Json.encodeToStream(player, it)
        }
    }
}