package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentAlbumsBinding

class AlbumsFragment : Fragment() {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlbumsViewModel
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AlbumsViewModel::class.java]

        adapter = AlbumAdapter { album ->
            val bundle = Bundle()
            bundle.putInt("albumId", album.albumId)
            findNavController().navigate(R.id.action_albumsFragment_to_albumDetailFragment, bundle)
        }

        binding.recyclerAlbums.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAlbums.adapter = adapter

        observeAlbums()
        viewModel.fetchAlbums()
    }

    private fun observeAlbums() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.updateAlbums(albums)
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