package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentAlbumDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

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
        if (albumId > 0) {
            viewModel.fetchAlbum(albumId)
        } else {
            Snackbar.make(binding.root, "ID de álbum inválido", Snackbar.LENGTH_LONG).show()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumReleaseDate.text = formatDate(album.releaseDate)
            binding.albumRecordLabel.text = album.recordLabel
            binding.albumDescription.text = album.description
            Glide.with(requireContext())
                .load(album.cover)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.albumCover)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        val albumId = arguments?.getInt("albumId") ?: 0
                        if (albumId > 0) viewModel.fetchAlbum(albumId)
                    }
                    .show()
            }
        }
    }

    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMM. yyyy", Locale("es", "CO"))
            val date = inputFormat.parse(isoDate) ?: return isoDate
            outputFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
