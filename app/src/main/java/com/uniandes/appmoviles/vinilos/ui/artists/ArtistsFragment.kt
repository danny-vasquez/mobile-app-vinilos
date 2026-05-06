package com.uniandes.appmoviles.vinilos.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentArtistsBinding

class ArtistsFragment : Fragment() {

    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArtistsViewModel
    private lateinit var adapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ArtistsViewModel::class.java]

        adapter = ArtistAdapter { artist ->
            val bundle = Bundle()
            bundle.putInt("artistId", artist.artistId)
            findNavController().navigate(R.id.action_artistsFragment_to_artistDetailFragment, bundle)
        }

        binding.recyclerArtists.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerArtists.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchArtists()
        }

        observeViewModel()
        viewModel.fetchArtists()
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            adapter.updateArtists(artists)
            val hasError = !viewModel.error.value.isNullOrBlank()
            binding.emptyView.visibility = if (artists.isEmpty() && !hasError) View.VISIBLE else View.GONE
            binding.recyclerArtists.visibility = if (artists.isNotEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) binding.swipeRefresh.isRefreshing = false
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                binding.emptyView.visibility = View.GONE
                Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) { viewModel.fetchArtists() }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
