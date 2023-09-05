package com.mcdane.box

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Timestamp
import java.util.*

class HighScoresAdapter(var data: List<ScoreRecord>) :
    RecyclerView.Adapter<HighScoresAdapter.ViewHolder>()
{
    constructor():
        this(ArrayList<ScoreRecord>())

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.name)
        val scoreView: TextView = view.findViewById(R.id.score)
        val timeView: TextView = view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.score_row, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        with (holder ){
            nameView.text = item.playerName
            scoreView.text = item.score.toString()
            timeView.text = item.time.toString()
        }
    }
}

class HighScoresActivity : AppCompatActivity() {
    private lateinit var scoreList: RecyclerView
    private lateinit var okButton: Button
    private val adapter = HighScoresAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        scoreList = findViewById(R.id.score_list)
        scoreList.layoutManager = LinearLayoutManager(this)
        scoreList.adapter = adapter
        initData()

        okButton = findViewById(R.id.score_ok_button)
        okButton.setOnClickListener{ onOKClicked() }
    }

    private fun onOKClicked() {
        finish()
    }

    private fun initData() {
        BoxDatabase.handler.post {
            val data = BoxDatabase.instance?.scoreDao()?.getTopRecords() ?: ArrayList<ScoreRecord>()
            adapter.data = data
            adapter.notifyDataSetChanged()
            Log.i(TAG, "data changed")
        }
    }
}