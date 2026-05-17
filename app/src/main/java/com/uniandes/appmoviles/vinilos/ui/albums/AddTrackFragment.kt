package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentAddTrackBinding

class AddTrackFragment : Fragment() {

    private var _binding: FragmentAddTrackBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddTrackViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = arguments?.getInt("albumId") ?: 0
        viewModel = ViewModelProvider(
            this,
            AddTrackViewModel.Factory(albumId, requireContext())
        )[AddTrackViewModel::class.java]

        setupTextWatchers()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            viewModel.addTrack(
                name = binding.etName.text.toString().trim(),
                duration = binding.etDuration.text.toString().trim()
            )
        }
    }

    private fun setupTextWatchers() {
        binding.etName.addTextChangedListener { binding.tilName.error = null }
        binding.etDuration.addTextChangedListener { binding.tilDuration.error = null }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddTrackUiState.Idle -> setLoadingState(false)
                is AddTrackUiState.Loading -> setLoadingState(true)
                is AddTrackUiState.Success -> {
                    setLoadingState(false)
                    setFragmentResult("track_added", Bundle())
                    Snackbar.make(
                        binding.root,
                        getString(R.string.msg_track_added),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
                is AddTrackUiState.Error -> {
                    setLoadingState(false)
                    showError(state)
                }
            }
        }
    }

    private fun showError(state: AddTrackUiState.Error) {
        when (state.field) {
            "name" -> binding.tilName.error = state.message
            "duration" -> binding.tilDuration.error = state.message
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
