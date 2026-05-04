package com.uniandes.appmoviles.vinilos.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        val artistId = arguments?.getInt("artistId") ?: 0
        if (artistId > 0) {
            viewModel.fetchArtist(artistId)
        } else {
            Snackbar.make(binding.root, "ID de artista inválido", Snackbar.LENGTH_LONG).show()
        }

        observeViewModel(artistId)
    }

    private fun observeViewModel(artistId: Int) {
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            binding.artistName.text = artist.name
            binding.artistBirthDate.text = formatDate(artist.birthDate)
            binding.artistDescription.text = artist.description
            Glide.with(requireContext())
                .load(artist.image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.artistImage)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        if (artistId > 0) viewModel.fetchArtist(artistId)
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
