package com.uniandes.appmoviles.vinilos.ui.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.appmoviles.vinilos.databinding.ItemArtistAlbumBinding
import com.uniandes.appmoviles.vinilos.model.Album
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistAlbumAdapter : RecyclerView.Adapter<ArtistAlbumAdapter.AlbumViewHolder>() {

    private var albums: List<Album> = emptyList()

    fun updateAlbums(newAlbums: List<Album>) {
        albums = newAlbums
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemArtistAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size

    class AlbumViewHolder(private val binding: ItemArtistAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            Glide.with(binding.albumCover.context)
                .load(album.cover)
                .centerCrop()
                .into(binding.albumCover)
            binding.albumName.text = album.name
            binding.albumYear.text = formatYear(album.releaseDate)
        }

        private fun formatYear(isoDate: String): String {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd"
            )
            for (format in formats) {
                try {
                    val date = SimpleDateFormat(format, Locale.getDefault()).parse(isoDate) ?: continue
                    return SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                } catch (_: Exception) { }
            }
            return isoDate.take(4).ifEmpty { isoDate }
        }
    }
}
