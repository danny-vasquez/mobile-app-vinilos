package com.uniandes.appmoviles.vinilos.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentArtistDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArtistDetailViewModel
    private lateinit var discographyAdapter: ArtistAlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ArtistDetailViewModel::class.java]

        setupRecyclerView()
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val artistId = arguments?.getInt("artistId") ?: 0
        val artistType = arguments?.getString("artistType") ?: "musician"

        if (artistId > 0) {
            viewModel.fetchArtist(artistId, artistType)
        } else {
            Snackbar.make(binding.root, "ID de artista inválido", Snackbar.LENGTH_LONG).show()
        }

        observeViewModel(artistId, artistType)
    }

    private fun setupRecyclerView() {
        discographyAdapter = ArtistAlbumAdapter()
        binding.discographyRecyclerView.apply {
            adapter = discographyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel(artistId: Int, artistType: String) {
        viewModel.artistDetail.observe(viewLifecycleOwner) { artist ->
            binding.artistTitle.text = artist.name
            binding.artistName.text = artist.name
            binding.artistDate.text = formatDate(artist.date)
            binding.artistDescription.text = artist.description
            Glide.with(requireContext())
                .load(artist.image)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                .into(binding.artistImage)
            discographyAdapter.updateAlbums(artist.albums)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        if (artistId > 0) viewModel.fetchArtist(artistId, artistType)
                    }
                    .show()
            }
        }
    }

    private fun formatDate(isoDate: String): String {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd"
        )
        for (format in formats) {
            try {
                val date = SimpleDateFormat(format, Locale.getDefault()).parse(isoDate) ?: continue
                return SimpleDateFormat("d MMM. yyyy", Locale("es", "CO")).format(date)
            } catch (_: Exception) { }
        }
        return isoDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
