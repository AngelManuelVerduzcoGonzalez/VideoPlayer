package com.example.videoplayer1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ListView
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
import android.app.AlertDialog
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var videosId: MutableList<Video>
    private lateinit var adapter: VideosAdapter
    private lateinit var videoPlayer: YouTubePlayer
    private lateinit var videoTitle: TextView
    private lateinit var videoThumbnail: ImageView

    private fun setupAddVideoButton() {
        findViewById<FloatingActionButton>(R.id.fabAddVideo).setOnClickListener {
            return@setOnClickListener showAddVideoDialog()
        }
    }

    private fun showAddVideoDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_video, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextVideoId)

        builder.setView(dialogLayout)
            .setPositiveButton("Agregar") { _, _ ->
                val videoId = editText.text.toString()
                if (videoId.isNotEmpty()) {
                    addVideo(Video(videoId))
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

        builder.show()
    }

    private fun addVideo(video: Video) {
        videosId.add(video)
        adapter.notifyDataSetChanged()
        playVideo(video)
    }

    private fun playVideo(video: Video) {
        videoPlayer.cueVideo(video.videoId, 0f)

        // Actualizar miniatura y título
        val thumbnailUrl = "https://img.youtube.com/vi/${video.videoId}/0.jpg"
        Glide.with(this).load(thumbnailUrl).into(videoThumbnail)

        thread {
            val videoInfo = getYouTubeVideoInfo(video.videoId)
            runOnUiThread {
                videoTitle.text = videoInfo?.getString("title") ?: "Título no disponible"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        videosId = mutableListOf(
            Video("ktBMxkLUIwY"),
            Video("2OnO3UXFZdE"),
            Video("mREOvIgImmo")
        )

        videoThumbnail = findViewById(R.id.tn)
        videoTitle = findViewById(R.id.titulo)
        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.videoPlayer)

        // Agregar ciclo de vida del YouTubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        // Cargar la miniatura del video
        val thumbnailUrl = "https://img.youtube.com/vi/${videosId[0].videoId}/0.jpg"
        Glide.with(this).load(thumbnailUrl).into(videoThumbnail)

        thread {
            val videoInfo = getYouTubeVideoInfo(videosId[0].videoId)
            runOnUiThread {
                videoTitle.text = videoInfo?.getString("title") ?: "Título no disponible"
            }
        }

        // Configurar el reproductor de YouTube para el nuevo video
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // Cue el video en el nuevo reproductor
                videoPlayer = youTubePlayer
                youTubePlayer.cueVideo(videosId[0].videoId, 0f)

                // Asignar el adaptador a la lista de videos
                val listaVideos = findViewById<ListView>(R.id.listaVideos)
                adapter = VideosAdapter(this@MainActivity, videosId, videoPlayer, videoTitle, videoThumbnail)
                listaVideos.adapter = adapter

                // Configurar el botón para agregar videos
                setupAddVideoButton()
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

