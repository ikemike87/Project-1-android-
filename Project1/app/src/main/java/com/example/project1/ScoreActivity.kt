package com.example.project1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import java.io.InputStream
import java.io.OutputStream


private const val EXTRA_CATEGORY = "com.example.project1.category"
private const val EXTRA_SCORE = "com.example.project1.score"
private const val EXTRA_ACTIVITY_SETUP = "com.example.project1.activity_setup"
private const val TAG = "ScoreActivity"

class ScoreActivity : AppCompatActivity() {

    private lateinit var finalScoreTextView: TextView
    private lateinit var userNameInput: EditText
    private lateinit var enterScoreButton: Button
    private lateinit var scoreButton: Button

    private lateinit var scoreTitleTextView: TextView
    private lateinit var buildingCategoryTextView: TextView
    private lateinit var buildingScoreTextView: TextView
    private lateinit var classesCategoryTextView: TextView
    private lateinit var classesScoreTextView: TextView
    private lateinit var facultyCategoryTextView: TextView
    private lateinit var facultyScoreTextView: TextView

    private lateinit var scoreTextViewList: List<TextView>

    private lateinit var homeButton: Button

    private var activitySetup: String = "ScoreList"
    private lateinit var category: String
    private lateinit var scoreViewModel: ScoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        finalScoreTextView = findViewById(R.id.final_score_text)
        userNameInput = findViewById(R.id.user_name_input)
        enterScoreButton = findViewById(R.id.enter_score_button)
        scoreButton = findViewById(R.id.score_button)

        scoreTitleTextView = findViewById(R.id.score_title_text_view)
        buildingCategoryTextView = findViewById(R.id.building_category_text_view)
        buildingScoreTextView = findViewById(R.id.building_scores_text_view)
        classesCategoryTextView = findViewById(R.id.classes_category_text_view)
        classesScoreTextView = findViewById(R.id.classes_scores_text_view)
        facultyCategoryTextView = findViewById(R.id.faculty_category_text_view)
        facultyScoreTextView = findViewById(R.id.faculty_scores_text_view)

        scoreTextViewList = listOf(
            scoreTitleTextView,
            buildingScoreTextView,
            buildingCategoryTextView,
            classesScoreTextView,
            classesCategoryTextView,
            facultyScoreTextView,
            facultyCategoryTextView
        )

        homeButton = findViewById(R.id.home_button)

        val buildingOutputStream = this.openFileOutput("BuildingScores", Context.MODE_PRIVATE)
        val classesOutputStream = this.openFileOutput("ClassesScores", Context.MODE_PRIVATE)
        val facultyOutputStream = this.openFileOutput("FacultyScores", Context.MODE_PRIVATE)

        val buildingInputStream = this.openFileInput("BuildingScores")
        val classesInputStream = this.openFileInput("ClassesScores")
        val facultyInputStream = this.openFileInput("FacultyScores")

        scoreViewModel = ViewModelProvider(this).get(ScoreViewModel::class.java)

        category = intent.getStringExtra(EXTRA_CATEGORY).toString()
        scoreViewModel.currentScore = intent.getIntExtra(EXTRA_SCORE, 0)
        activitySetup = intent.getStringExtra(EXTRA_ACTIVITY_SETUP).toString()

        when (activitySetup) {
            "ScoreList" -> {
                setScores(buildingInputStream, classesInputStream, facultyInputStream)
            }
            "finalScore" -> {
                showFinalScoreView()
                setFinalScoreText()
            }
            else -> {
                setScores(buildingInputStream, classesInputStream, facultyInputStream)
            }
        }

        scoreButton.setOnClickListener {
            setScores(buildingInputStream, classesInputStream, facultyInputStream)
        }

        enterScoreButton.setOnClickListener {
            enterPlayerScoreData(buildingOutputStream, classesOutputStream, facultyOutputStream)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setScores(buildingInputStream: InputStream, classesInputStream: InputStream, facultyInputStream: InputStream) {
        showScoreListView()
        setScoreTextView(buildingInputStream, buildingScoreTextView)
        setScoreTextView(classesInputStream, classesScoreTextView)
        setScoreTextView(facultyInputStream, facultyScoreTextView)
    }

    private fun showFinalScoreView() {
        for (textView in scoreTextViewList) {
            textView.visibility = View.GONE
        }

        finalScoreTextView.visibility = View.VISIBLE
        userNameInput.visibility = View.VISIBLE
        enterScoreButton.visibility = View.VISIBLE
        scoreButton.visibility = View.VISIBLE
    }

    private fun showScoreListView() {
        for (textView in scoreTextViewList) {
            textView.visibility = View.VISIBLE
        }

        finalScoreTextView.visibility = View.GONE
        userNameInput.visibility = View.GONE
        enterScoreButton.visibility = View.GONE
        scoreButton.visibility = View.GONE
    }


    private fun enterPlayerScoreData(buildingOutputStream: OutputStream, classesOutputStream: OutputStream, facultyOutputStream: OutputStream) {
        enterScoreButton.isEnabled = false
        enterScoreButton.isClickable = false

        scoreViewModel.playerName = userNameInput.text.toString()
        val playerScore = "${scoreViewModel.playerName}: ${scoreViewModel.currentScore}\n"

        //https://en.proft.me/2019/10/14/reading-and-writing-files-android-using-kotlin/
        when (category) {
            "Building.txt" -> {
                buildingOutputStream.use { output ->
                    output.write(playerScore.toByteArray())
                }
            }
            "Classes.txt" -> {
                classesOutputStream.use { output ->
                    output.write(playerScore.toByteArray())
                }
            }
            "Faculty.txt" -> {
                facultyOutputStream.use { output ->
                    output.write(playerScore.toByteArray())
                }
            }
        }
    }

    private fun setScoreTextView(inputStream: InputStream, textView: TextView) {
        inputStream.use { stream ->
            val text = stream.bufferedReader().use {
                it.readText()
            }
            textView.text = text
        }
    }

    private fun setFinalScoreText() {
        val finalScoreString = "Score: ${scoreViewModel.currentScore}"
        finalScoreTextView.text = finalScoreString
    }

    companion object {
        fun newIntent(
            packageContext: Context,
            category: String,
            score: Int,
            activitySetup: String
        ): Intent {
            return Intent(packageContext, ScoreActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY, category)
                putExtra(EXTRA_SCORE, score)
                putExtra(EXTRA_ACTIVITY_SETUP, activitySetup)
            }
        }
    }
}