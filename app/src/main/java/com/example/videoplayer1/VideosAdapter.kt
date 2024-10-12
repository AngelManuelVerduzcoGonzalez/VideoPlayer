package com.example.videoplayer1

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class VideosAdapter(private val mContext: Context, private val listaVideos: MutableList<Video>, private val videoPlayer: YouTubePlayer, private val videoTitle: TextView, private val videoThumbnail: ImageView) : ArrayAdapter<Video>(mContext, 0, listaVideos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = convertView ?: LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false)

        val video = listaVideos[position]

        // Cargar la miniatura del video
        val thumbnailUrl = "https://img.youtube.com/vi/${video.videoId}/0.jpg"
        Glide.with(mContext).load(thumbnailUrl).into(layout.findViewById(R.id.thumbnail))

        // Obtener la información del video en segundo plano
        thread {
            val videoInfo = getYouTubeVideoInfo(video.videoId)

            // Usar Handler para actualizar el UI en el hilo principal
            Handler(Looper.getMainLooper()).post {
                layout.findViewById<TextView>(R.id.title).text = videoInfo?.getString("title") ?: "Título no disponible"
            }
        }

        layout.setOnClickListener {
            thread {
                val videoInfo = getYouTubeVideoInfo(video.videoId)

                // Usar Handler para actualizar el UI en el hilo principal
                Handler(Looper.getMainLooper()).post {
                    videoTitle.text = videoInfo?.getString("title") ?: "Título no disponible"
                }
            }

            // Cargar la miniatura del video
            Glide.with(mContext).load(thumbnailUrl).into(videoThumbnail)

            videoPlayer?.cueVideo(video.videoId, 0f)
        }

        layout.setOnLongClickListener() {
            showPopupMenu(layout, position)
            true
        }

        return layout
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

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(mContext, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_update -> {
                    Toast.makeText(mContext, "Actualizar video", Toast.LENGTH_SHORT).show()
                    // Lógica de actualización
                    true
                }
                R.id.action_delete -> {
                    Toast.makeText(mContext, "Eliminar video", Toast.LENGTH_SHORT).show()
                    eliminarVideo(position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun eliminarVideo(position: Int) {
        // Eliminar el elemento de la lista
        listaVideos.removeAt(position)
        notifyDataSetChanged()
    }
}
