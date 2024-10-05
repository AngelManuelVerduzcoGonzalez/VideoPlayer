package com.example.videoplayer1

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class MainActivity : AppCompatActivity() {
    private lateinit var VideosId: List<Video>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        VideosId = listOf(
            Video("ktBMxkLUIwY"),
            Video("2OnO3UXFZdE"),
            Video("mREOvIgImmo")
        )

        val listaVideos = findViewById<ListView>(R.id.listaVideos)
        val adapter = VideosAdapter(this, VideosId, lifecycle)
        listaVideos.adapter = adapter

    }
}