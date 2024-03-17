package com.example.simonssaysmemory

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {
    private var soundPool: SoundPool
    private var soundIds = IntArray(4)

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        soundIds[0] = soundPool.load(context, R.raw.cow, 1)
        soundIds[1] = soundPool.load(context, R.raw.red, 1)
        soundIds[2] = soundPool.load(context, R.raw.pipe, 1)
        soundIds[3] = soundPool.load(context, R.raw.pumpkin, 1)    }

    fun playSound(soundIndex: Int) {
        println("Sound")
        soundPool.play(soundIds[soundIndex], 1.0f, 1.0f, 1, 0, 1.0f)
    }
}