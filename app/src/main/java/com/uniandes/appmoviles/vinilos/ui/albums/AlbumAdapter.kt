package com.uniandes.appmoviles.vinilos.ui.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.appmoviles.vinilos.databinding.ItemAlbumBinding
import com.uniandes.appmoviles.vinilos.model.Album

class AlbumAdapter(private val onClickListener: (Album) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private var albums = listOf<Album>()

    fun updateAlbums(newAlbums: List<Album>) {
        albums = newAlbums
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount() = albums.size

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumReleaseDate.text = album.releaseDate
            Glide.with(binding.root.context)
                .load(album.cover)
                .into(binding.albumCover)
            binding.root.setOnClickListener {
                onClickListener(album)
            }
        }
    }
}