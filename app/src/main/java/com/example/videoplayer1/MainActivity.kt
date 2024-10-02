package com.example.videoplayer1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val VIDEO_ID = "wTaO2vmi3Rs"  // Reemplaza con el ID del video que desees

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val videoThumbnail: ImageView = findViewById(R.id.video_thumbnail)
        val videoTitle: TextView = findViewById(R.id.video_title)
        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)

        // Agregar ciclo de vida del YouTubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        // Cargar la miniatura del video de YouTube
        val thumbnailUrl = "https://img.youtube.com/vi/$VIDEO_ID/0.jpg"
        Glide.with(this).load(thumbnailUrl).into(videoThumbnail)

        // Obtener y mostrar el título del video usando la API de YouTube
        thread {
            val videoInfo = getYouTubeVideoInfo(VIDEO_ID)
            runOnUiThread {
                videoTitle.text = videoInfo?.getString("title") ?: "Título no disponible"
            }
        }

        // Configurar el reproductor de YouTube
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(VIDEO_ID, 0f)
            }
        })

    }
    // Función para obtener la información del video
    private fun getYouTubeVideoInfo(videoId: String): JSONObject? {
        return try {
            val url = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
            val json = URL(url).readText()
            JSONObject(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}