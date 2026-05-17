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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.FragmentCreateAlbumBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateAlbumViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            CreateAlbumViewModel.Factory(requireContext())
        )[CreateAlbumViewModel::class.java]

        setupDropdowns()
        setupDateField()
        setupTextWatchers()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            viewModel.createAlbum(
                name = binding.etName.text.toString().trim(),
                cover = binding.etCover.text.toString().trim(),
                releaseDate = binding.etReleaseDate.text.toString().trim(),
                description = binding.etDescription.text.toString().trim(),
                genre = binding.actvGenre.text.toString().trim(),
                recordLabel = binding.actvRecordLabel.text.toString().trim()
            )
        }
    }

    private fun setupDropdowns() {
        val genres = resources.getStringArray(R.array.genres)
        val genreAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, genres)
        binding.actvGenre.setAdapter(genreAdapter)

        val labels = resources.getStringArray(R.array.record_labels)
        val labelAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, labels)
        binding.actvRecordLabel.setAdapter(labelAdapter)
    }

    private fun setupDateField() {
        val openPicker = View.OnClickListener { showDatePicker() }
        binding.etReleaseDate.setOnClickListener(openPicker)
        binding.tilReleaseDate.setEndIconOnClickListener(openPicker)
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.label_release_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            binding.etReleaseDate.setText(sdf.format(Date(selection)))
            binding.tilReleaseDate.error = null
        }

        picker.show(parentFragmentManager, "date_picker")
    }

    private fun setupTextWatchers() {
        binding.etName.addTextChangedListener { binding.tilName.error = null }
        binding.etCover.addTextChangedListener { binding.tilCover.error = null }
        binding.etDescription.addTextChangedListener { binding.tilDescription.error = null }
        binding.actvGenre.addTextChangedListener { binding.tilGenre.error = null }
        binding.actvRecordLabel.addTextChangedListener { binding.tilRecordLabel.error = null }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreateAlbumUiState.Idle -> setLoadingState(false)
                is CreateAlbumUiState.Loading -> setLoadingState(true)
                is CreateAlbumUiState.Success -> {
                    setLoadingState(false)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.msg_album_created),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
                is CreateAlbumUiState.Error -> {
                    setLoadingState(false)
                    showError(state)
                }
            }
        }
    }

    private fun showError(state: CreateAlbumUiState.Error) {
        when (state.field) {
            "name" -> binding.tilName.error = state.message
            "cover" -> binding.tilCover.error = state.message
            "releaseDate" -> binding.tilReleaseDate.error = state.message
            "description" -> binding.tilDescription.error = state.message
            "genre" -> binding.tilGenre.error = state.message
            "recordLabel" -> binding.tilRecordLabel.error = state.message
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
