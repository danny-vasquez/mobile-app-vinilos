package com.uniandes.appmoviles.vinilos.ui.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.ItemArtistAlbumBinding
import com.uniandes.appmoviles.vinilos.model.Album
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistAlbumAdapter : RecyclerView.Adapter<ArtistAlbumAdapter.AlbumViewHolder>() {

    private var albums: List<Album> = emptyList()

    fun updateAlbums(newAlbums: List<Album>) {
        val diff = DiffUtil.calculateDiff(AlbumDiffCallback(albums, newAlbums))
        albums = newAlbums
        diff.dispatchUpdatesTo(this)
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
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .transition(DrawableTransitionOptions.withCrossFade())
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

    private class AlbumDiffCallback(
        private val oldList: List<Album>,
        private val newList: List<Album>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].albumId == newList[newPos].albumId
        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos] == newList[newPos]
    }
}
