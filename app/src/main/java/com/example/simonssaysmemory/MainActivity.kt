package com.example.simonssaysmemory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var soundManager: SoundManager
    var isClickBloqued = false
    var isHightlighting = false
    var blockGenerateSequence = false
    var blockClickColors = false

    var gameSequences = ArrayList<Int>()
    var userSquence = 0
    var currentMaxSequence = 1
    val maxSequenceLenght = 25
    var gameStarted = false
    var currentScore = 0
    var generateSequenceText = "Start Game"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customView = CustomView(this)
        soundManager = SoundManager(this)
        setContentView(customView)
    }

    inner class CustomView(context: Context) : View(context) {
        private val paint = Paint()


        init {
            paint.color = Color.parseColor("#6322A7")
            paint.style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val screenWidth = width.toFloat()
            val screenHeight = height.toFloat()
            canvas.drawColor(Color.BLACK)

            val squareSize = screenWidth.coerceAtMost(screenHeight) * 0.8f
            val margin = 70f


            val buttonSize = (squareSize - margin * 3) / 2.8f // Tamaño de cada botón
            val colors = arrayOf("#FFFF00", "#FF0000", "#0000FF", "#00FF00") // Amarillo, Rojo, Azul, Verde

            val left = (screenWidth - squareSize) / 2
            val top = (screenHeight - squareSize) / 2
            val right = left + squareSize
            val bottom = top + squareSize


            canvas.drawRect(left, top, right, bottom, paint)


            paint.color = Color.WHITE

            val startX = left + (squareSize - (buttonSize * 2 + margin)) / 2
            val startY = top + (squareSize - (buttonSize * 2 + margin)) / 2
            var currentColor = 0

            for (row in 0 until 2) {
                for (col in 0 until 2) {
                    val buttonLeft = startX + col * (buttonSize + margin)
                    val buttonTop = startY + row * (buttonSize + margin)
                    val buttonRight = buttonLeft + buttonSize
                    val buttonBottom = buttonTop + buttonSize
                    paint.color = Color.parseColor(colors[currentColor])
                    canvas.drawRect(buttonLeft, buttonTop, buttonRight, buttonBottom, paint)
                    currentColor++
                }
            }

            paint.color = Color.WHITE


            val titleText = "SIMON SAYS"
            paint.textSize = 100f
            val titleX = (screenWidth - paint.measureText(titleText)) / 2
            val titleY = screenHeight * 0.2f
            canvas.drawText(titleText, titleX, titleY, paint)

            paint.textSize = 70f
            // Dibujar texto de puntuación en la parte inferior derecha de la pantalla
            val scoreText = "Score: "+ currentScore+ ""
            val maxScoreText = "Max Score: 25"
            val scoreMaxScoreX = (screenWidth - paint.measureText(titleText)) / 2
            val scoreY = screenHeight * 0.9f
            val maxScoreY = scoreY + paint.textSize
            canvas.drawText(scoreText, scoreMaxScoreX, scoreY, paint)
            canvas.drawText(maxScoreText, scoreMaxScoreX, maxScoreY, paint)

            val buttonSize2 = (squareSize - margin * 3) / 6f
            val generateButtonLeft = margin
            val generateButtonTop = margin
            val generateButtonRight = squareSize - margin // Ancho del botón igual al ancho del cuadrado menos los márgenes
            val generateButtonBottom = generateButtonTop + buttonSize2 * 2 // Altura del botón será el doble del tamaño original
            canvas.drawRect(generateButtonLeft, generateButtonTop, generateButtonRight, generateButtonBottom, paint)

            paint.color = Color.BLACK
            paint.textSize = 72f // Aumentar el tamaño del texto
            val generateButtonText = generateSequenceText
            val generateButtonTextWidth = paint.measureText(generateButtonText)
            val generateButtonTextHeight = paint.descent() - paint.ascent()
            val generateButtonTextX = generateButtonLeft + (generateButtonRight - generateButtonLeft - generateButtonTextWidth) / 2
            val generateButtonTextY = generateButtonTop + (generateButtonBottom - generateButtonTop + generateButtonTextHeight) / 2
            canvas.drawText(generateButtonText, generateButtonTextX, generateButtonTextY, paint)

        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN && !isClickBloqued) {

                val screenWidth = width.toFloat()
                val screenHeight = height.toFloat()
                val squareSize = screenWidth.coerceAtMost(screenHeight) * 0.8f
                val margin = 40f
                val buttonSize = (squareSize - margin * 3) / 2.8f
                val left = (screenWidth - squareSize) / 2
                val top = (screenHeight - squareSize) / 2


                val clickX = event.x
                val clickY = event.y


                val startX = left + margin / 2
                val startY = top + margin


                val row = ((clickY - startY) / (buttonSize + margin)).toInt()
                val col = ((clickX - startX) / (buttonSize + margin)).toInt()


                if (row in 0 until 2 && col in 0 until 2 && !blockClickColors) {
                    val index = row * 2 + col
                    when (index) {
                        0 ->{
                            println("Se hizo clic en el botón amarillo")
                            highlightButton(Color.YELLOW, 1000)
                            compareClick(0)
                        }

                        1 -> {
                            println("Se hizo clic en el botón rojo")
                            highlightButton(Color.RED, 1000)
                            compareClick(1)
                        }
                        2 -> {
                            println("Se hizo clic en el botón azul")
                            highlightButton(Color.BLUE, 1000)
                            compareClick(2)
                        }
                        3 -> {
                            println("Se hizo clic en el botón verde")
                            highlightButton(Color.GREEN, 1000)
                            compareClick(3)
                        }
                    }
                } else {
                    if (clickY <= screenHeight * 0.1f && !blockGenerateSequence) {
                        if(!gameStarted) {
                            startGame()
                            println("GameStarted")
                        } else {
                            continueGame()
                            println("GameContinue")
                        }

                    }

                }

            }
            return super.onTouchEvent(event)
        }

        fun highlightButton(highlightColor: Int, durationMillis: Long) {

            isClickBloqued = true
            isHightlighting = true
            paint.color = Color.parseColor("#6322A7")

            paint.color = highlightColor
            when (highlightColor) {
                Color.YELLOW -> {
                    soundManager.playSound(0)
                }
                Color.RED -> {
                    soundManager.playSound(1)
                }
                Color.BLUE -> {
                    soundManager.playSound(2)
                }
                Color.GREEN -> {
                    soundManager.playSound(3)
                }

            }


            invalidate()

            postDelayed({
                paint.color = Color.parseColor("#6322A7")
                isClickBloqued = false
                invalidate()
            }, durationMillis)
        }

        fun createGame () {
            gameStarted = true
            gameSequences.clear()
            repeat(maxSequenceLenght) {
                gameSequences.add(Random.nextInt(4))
            }
            println(gameSequences)
        }

        fun startGame() {
            blockClickColors = true
            createGame()
            generateSequenceText = "Playing"
            currentMaxSequence = 0
            showSequence()
        }

        fun continueGame() {
            currentMaxSequence++
            generateSequenceText = "Playing"
            invalidate()
            showSequence()
        }

        fun showSequence() {
            isClickBloqued = true
            val handler = Handler(Looper.getMainLooper())
            var i = 0

            val runnable = object : Runnable {
                override fun run() {
                    if (i <= currentMaxSequence) {
                        when (gameSequences[i]) {
                            0 -> {
                                highlightButton(Color.YELLOW, 1000)
                            }
                            1 -> {
                                highlightButton(Color.RED, 1000)
                            }
                            2 -> {
                                highlightButton(Color.BLUE, 1000)
                            }
                            3 -> {
                                highlightButton(Color.GREEN, 1000)
                            }
                        }
                        i++
                        handler.postDelayed(this, 2000) // Espera 2 segundos antes de la próxima iteración
                    } else {
                        isClickBloqued = false
                        blockGenerateSequence = true
                        blockClickColors = false
                    }
                }
            }

            handler.post(runnable)
        }

        fun compareClick(selected: Int) {
            if(selected != gameSequences[userSquence]) {
                loseGame()
                return
            } else {
                println("Correct")
                if(userSquence == currentMaxSequence) {
                println("Can go to next sequence")
                currentScore = userSquence + 1
                    generateSequenceText = "Next Sequence"
                invalidate()
                userSquence = 0
                blockGenerateSequence = false
                    blockClickColors = true
                } else {
                    userSquence++
                }
            }
        }

        fun loseGame() {
            blockGenerateSequence = false
            blockClickColors = true
            println("Lost")
            currentScore = 0
            userSquence = 0
            currentMaxSequence = 0
            generateSequenceText = "You lost, reestart Game"
            gameStarted = false
            invalidate()

        }


    }

}



