package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentAddCollectorAlbumBinding
import com.uniandes.appmoviles.vinilos.model.Collector

class AddCollectorAlbumFragment : Fragment() {

    private var _binding: FragmentAddCollectorAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddCollectorAlbumViewModel

    private var collectors: List<Collector> = emptyList()
    private var selectedCollectorId: Int = -1

    private val statusOptions = listOf("Active", "Inactive")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCollectorAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = arguments?.getInt("albumId") ?: 0
        viewModel = ViewModelProvider(
            this,
            AddCollectorAlbumViewModel.Factory(albumId, requireContext())
        )[AddCollectorAlbumViewModel::class.java]

        setupStatusDropdown()
        setupTextWatchers()
        observeViewModel()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnSave.setOnClickListener {
            viewModel.addAlbumToCollector(
                collectorId = selectedCollectorId,
                priceText = binding.etPrice.text.toString(),
                status = binding.actvStatus.text.toString()
            )
        }
    }

    private fun setupStatusDropdown() {
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions)
        binding.actvStatus.setAdapter(statusAdapter)
    }

    private fun setupCollectorDropdown(collectors: List<Collector>) {
        this.collectors = collectors
        val names = collectors.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
        binding.actvCollector.setAdapter(adapter)
        binding.actvCollector.setOnItemClickListener { _, _, position, _ ->
            selectedCollectorId = collectors[position].id
            binding.tilCollector.error = null
        }
    }

    private fun setupTextWatchers() {
        binding.etPrice.addTextChangedListener { binding.tilPrice.error = null }
        binding.actvStatus.addTextChangedListener { binding.tilStatus.error = null }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddCollectorAlbumUiState.LoadingCollectors -> setLoadingState(true)
                is AddCollectorAlbumUiState.Ready -> {
                    setLoadingState(false)
                    setupCollectorDropdown(state.collectors)
                }
                is AddCollectorAlbumUiState.Saving -> setLoadingState(true)
                is AddCollectorAlbumUiState.Success -> {
                    setLoadingState(false)
                    Snackbar.make(binding.root, getString(R.string.msg_collector_album_added), Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is AddCollectorAlbumUiState.Error -> {
                    setLoadingState(false)
                    showError(state)
                }
            }
        }
    }

    private fun showError(state: AddCollectorAlbumUiState.Error) {
        when (state.field) {
            "collector" -> binding.tilCollector.error = state.message
            "price" -> binding.tilPrice.error = state.message
            "status" -> binding.tilStatus.error = state.message
            else -> Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.btnSave.isEnabled = !loading
        binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
