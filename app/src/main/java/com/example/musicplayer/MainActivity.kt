package com.example.musicplayer

import SoundItem
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private val handler = Handler()
    private var isPlaying = false

    private val soundList = listOf(
        SoundItem("Cancion numero 1", R.raw.sound1),
        SoundItem("Cancion numero 2", R.raw.sound2),
        // Add more sound items as needed
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
        totalTimeTextView = findViewById(R.id.totalTimeTextView)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = SoundAdapter(soundList, R.layout.item_sound)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { soundName ->
            showAlertDialog(soundName)
        }

        playButton.setOnClickListener {
            playSound(soundList[0].resourceId) // Play the first sound in the list
        }

        pauseButton.setOnClickListener {
            pauseSound()
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            val totalTime = mediaPlayer.duration
            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong() - TimeUnit.MINUTES.toMillis(minutes))
            totalTimeTextView.text = String.format("%02d:%02d", minutes, seconds)
            isPlaying = true
            mediaPlayer.start()
            updateSeekBar()
        }

        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            playButton.isEnabled = true
            pauseButton.isEnabled = false
            mediaPlayer.seekTo(0)
            currentTimeTextView.text = "00:00"
        }
    }

    private fun updateSeekBar() {
        handler.postDelayed({
            if (isPlaying) {
                val currentTime = mediaPlayer.currentPosition
                val minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong() - TimeUnit.MINUTES.toMillis(minutes))
                currentTimeTextView.text = String.format("%02d:%02d", minutes, seconds)
                updateSeekBar()
            }
        }, 1000) // Update every second
    }

    private fun playSound(soundResourceId: Int) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, Uri.parse("android.resource://${packageName}/${soundResourceId}"))
        mediaPlayer.prepareAsync()

        playButton.isEnabled = false
        pauseButton.isEnabled = true
    }

    private fun pauseSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
            playButton.isEnabled = true
            pauseButton.isEnabled = false
        }
    }

    private fun showAlertDialog(soundName: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Reproducir $soundName")
        alertDialogBuilder.setMessage("¿Deseas reproducir la canción $soundName?")
        alertDialogBuilder.setPositiveButton("Play") { _, _ ->
            val soundItem = soundList.find { it.name == soundName }
            soundItem?.let {
                playSound(it.resourceId)
            }
        }
        alertDialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
}