package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.uniandes.appmoviles.vinilos.databinding.FragmentAlbumDetailBinding

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlbumDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AlbumDetailViewModel::class.java]

        val albumId = arguments?.getInt("albumId") ?: 0
        viewModel.fetchAlbum(albumId)

        observeAlbum()
    }

    private fun observeAlbum() {
        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumReleaseDate.text = album.releaseDate
            binding.albumRecordLabel.text = album.recordLabel
            binding.albumDescription.text = album.description
            Glide.with(requireContext())
                .load(album.cover)
                .into(binding.albumCover)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}