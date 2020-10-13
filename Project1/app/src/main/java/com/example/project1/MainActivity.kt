package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var buildingButton: Button
    private lateinit var classesButton: Button
    private lateinit var facultyButton: Button
    private lateinit var scoreButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildingButton = findViewById(R.id.building_button)
        classesButton = findViewById(R.id.classes_button)
        facultyButton = findViewById(R.id.faculty_button)
        scoreButton = findViewById(R.id.score_button)

        buildingButton.setOnClickListener {
           startIntent("Building.txt")
        }

        classesButton.setOnClickListener {
            startIntent("Classes.txt")
        }

        facultyButton.setOnClickListener {
            startIntent("Faculty.txt")
        }

        scoreButton.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startIntent(fileName: String) {
        val intent = QuestionAnswerActivity.newIntent(this@MainActivity, fileName)
        startActivity(intent)
    }
}