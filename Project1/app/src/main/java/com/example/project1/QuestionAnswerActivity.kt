package com.example.project1

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.io.BufferedReader

private const val EXTRA_FILE_NAME = "com.example.project1.file_name"
private const val TAG = "QuestionAnswerActivity"

class QuestionAnswerActivity : AppCompatActivity() {

    private lateinit var answer1Button: Button
    private lateinit var answer2Button: Button
    private lateinit var answer3Button: Button
    private lateinit var answer4Button: Button
    private lateinit var nextButton: Button
    private lateinit var hintButton: Button
    private lateinit var questionText: TextView
    private lateinit var scoreText: TextView
    private lateinit var answer1ImageButton: ImageButton
    private lateinit var answer2ImageButton: ImageButton
    private lateinit var answer3ImageButton: ImageButton
    private lateinit var answer4ImageButton: ImageButton

    private lateinit var answerButtons: List<Button>
    private lateinit var answerImageButtons: List<ImageButton>

    private lateinit var fileName: String
    private lateinit var stream: BufferedReader
    private lateinit var questionList: List<String>

    private var currentScore = 0

    private var questionNumber = 0
    private lateinit var question: String
    private lateinit var answer: String
    private var answerList = mutableListOf<String>()
    private lateinit var questionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_answer)

        answer1Button = findViewById(R.id.answer1_button)
        answer2Button = findViewById(R.id.answer2_button)
        answer3Button = findViewById(R.id.answer3_button)
        answer4Button = findViewById(R.id.answer4_button)
        nextButton = findViewById(R.id.next_button)
        hintButton = findViewById(R.id.hint_button)
        questionText = findViewById(R.id.question_text)
        scoreText = findViewById(R.id.score)
        answer1ImageButton = findViewById(R.id.answer1_image_button)
        answer2ImageButton = findViewById(R.id.answer2_image_button)
        answer3ImageButton = findViewById(R.id.answer3_image_button)
        answer4ImageButton = findViewById(R.id.answer4_image_button)

        answerButtons = listOf(answer1Button, answer2Button, answer3Button, answer4Button)
        answerImageButtons = listOf(answer1ImageButton, answer2ImageButton, answer3ImageButton, answer4ImageButton)

        fileName = intent.getStringExtra(EXTRA_FILE_NAME).toString()

        stream = assets.open(fileName).bufferedReader()

        questionList = stream.readLines()

        setQuestionsAnswers()

        currentScore = 0

        answer1Button.setOnClickListener {
            // check answer
            checkAnswer(answer1Button.text)
        }

        answer2Button.setOnClickListener {
            // check answer
            checkAnswer(answer2Button.text)
        }

        answer3Button.setOnClickListener {
            // check answer
            checkAnswer(answer3Button.text)
        }

        answer4Button.setOnClickListener {
            // check answer
            checkAnswer(answer4Button.text)
        }

        nextButton.setOnClickListener {
            // go to next question
            if (questionNumber == 9) {
                val intent = ScoreActivity.newIntent(this@QuestionAnswerActivity, fileName, currentScore, "finalScore")
                startActivity(intent)
            }
            else {
                questionNumber++
                setQuestionsAnswers()
                when (questionType) {
                    "." -> {
                        for (button in answerButtons) {
                            disableOrEnableButton(button, true, true)
                        }
                    }
                    "?" -> {
                        for (imageButton in answerImageButtons) {
                            disableOrEnableImageButton(imageButton, true, true)
                        }
                    }
                }
                disableOrEnableButton(nextButton, false, false)
            }
        }

        hintButton.setOnClickListener {
            // remove one wrong answer randomly
            useHint()
        }
    }

    private fun useHint() {
        disableOrEnableButton(hintButton, false, false, View.GONE)
        when {
            answer != answer1Button.text ->
                disableOrEnableButton(answer1Button, false, false, View.GONE)
            answer != answer2Button.text ->
                disableOrEnableButton(answer2Button, false, false, View.GONE)
            answer != answer3Button.text ->
                disableOrEnableButton(answer3Button, false, false, View.GONE)
            answer != answer4Button.text ->
                disableOrEnableButton(answer4Button, false, false, View.GONE)
        }
    }

    private fun checkAnswer(selectedAnswer: CharSequence) {
        for (button in answerButtons) {
            disableOrEnableButton(button, false, false)
        }
        disableOrEnableButton(nextButton, true, true)
        if ( selectedAnswer == answer ) {
            //https://developer.android.com/guide/topics/media/mediaplayer
            val correctSound: MediaPlayer? = MediaPlayer.create(this, R.raw.correct_answer)
            correctSound?.start()

            currentScore++
            val scoreString =  "Score: $currentScore"
            scoreText.text = scoreString
        }
        else {
            val wrongSound: MediaPlayer? = MediaPlayer.create(this, R.raw.wrong_answer)
            wrongSound?.start()
        }
    }

    private fun disableOrEnableButton(button: Button,
                                      enable: Boolean,
                                      clickable: Boolean,
                                      visibility: Int = View.VISIBLE) {
        button.isEnabled = enable
        button.isClickable = clickable
        button.visibility = visibility
    }

    private fun disableOrEnableImageButton(imageButton: ImageButton,
                                      enable: Boolean,
                                      clickable: Boolean,
                                      visibility: Int = View.VISIBLE) {
        imageButton.isEnabled = enable
        imageButton.isClickable = clickable
        imageButton.visibility = visibility
    }

    private fun setQuestionsAnswers() {
        val questionNum = questionNumber * 5

        for (i in questionNum..questionNum + 4) {
            when (questionList[i][0]) {
                '.' -> reformatQuestion(i)
                '!' -> reformatQuestion(i)
                '?' -> reformatQuestion(i)
                '*' -> {
                    val reformattedAnswer = questionList[i]//.drop(1)
                    answer = reformattedAnswer
                    answerList.add(reformattedAnswer)
                }
                else -> {
                    answerList.add(questionList[i])
                }
            }
        }

        when (questionType) {
            "." -> {
                for (button in answerButtons) {
                    disableOrEnableButton(button, true, true)
                }

                for (imageButton in answerImageButtons) {
                    disableOrEnableImageButton(imageButton, false, false, View.GONE)
                }

                questionText.text = question
                for ((i, button) in answerButtons.withIndex()) {
                    button.text = answerList[i]
                }
                answerList.clear()
            }/*
            "!" -> {
                for (button in answerButtons) {
                    disableOrEnableButton(button, false, false, View.GONE)
                }

                for (imageButton in answerImageButtons) {
                    disableOrEnableImageButton(imageButton, true, true)
                }

                questionText.text = question
                for ((i, imageButton) in answerImageButtons.withIndex()) {
                    imageButton.setImageResource(R.drawable.answerList[i])
                }
                answerList.clear()
            }*/
        }
    }

    private fun reformatQuestion(index: Int) {
        questionType = questionList[index][0].toString()
        val reformattedQuestion = questionList[index].drop(1)
        question = reformattedQuestion
    }

    companion object {
        fun newIntent(packageContext: Context, fileName: String): Intent {
            return Intent(packageContext, QuestionAnswerActivity::class.java).apply {
                putExtra(EXTRA_FILE_NAME, fileName)
            }
        }
    }
}