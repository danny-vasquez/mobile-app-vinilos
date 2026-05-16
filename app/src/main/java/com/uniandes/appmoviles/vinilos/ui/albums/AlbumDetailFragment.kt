package com.uniandes.appmoviles.vinilos.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.uniandes.appmoviles.vinilos.R
import com.uniandes.appmoviles.vinilos.databinding.DialogAddCommentBinding
import com.uniandes.appmoviles.vinilos.databinding.FragmentAlbumDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlbumDetailViewModel
    private val commentAdapter = CommentAdapter()
    private val trackAdapter = TrackAdapter()
    private var albumId = 0

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

        setupRecyclerView()

        albumId = arguments?.getInt("albumId") ?: 0
        if (albumId > 0) {
            viewModel.fetchAlbum(albumId)
        } else {
            Snackbar.make(binding.root, "ID de álbum inválido", Snackbar.LENGTH_LONG).show()
        }

        setFragmentResultListener("track_added") { _, _ ->
            if (albumId > 0) viewModel.fetchAlbum(albumId)
        }

        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnAddComment.setOnClickListener { showAddCommentDialog() }
        binding.btnAddTrack.setOnClickListener {
            if (albumId > 0) {
                val bundle = Bundle().apply { putInt("albumId", albumId) }
                findNavController().navigate(R.id.action_albumDetailFragment_to_addTrackFragment, bundle)
            }
        }
    }

    private fun showAddCommentDialog() {
        val dialogBinding = DialogAddCommentBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val name = dialogBinding.etName.text.toString()
                val telephone = dialogBinding.etTelephone.text.toString()
                val email = dialogBinding.etEmail.text.toString()
                val description = dialogBinding.etDescription.text.toString()
                val rating = dialogBinding.ratingBar.rating.toInt()

                if (name.isBlank() || telephone.isBlank() || email.isBlank() || description.isBlank() || rating == 0) {
                    Snackbar.make(dialogBinding.root, R.string.error_empty_fields, Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addComment(albumId, name, telephone, email, description, rating)
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    private fun observeViewModel() {
        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.albumTitle.text = album.name
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumReleaseDate.text = formatDate(album.releaseDate)
            binding.albumRecordLabel.text = album.recordLabel
            binding.albumDescription.text = album.description
            trackAdapter.updateTracks(album.tracks)
            Glide.with(requireContext())
                .load(album.cover)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                .into(binding.albumCover)
        }
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.updateComments(comments)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        if (albumId > 0) viewModel.fetchAlbum(albumId)
                    }
                    .show()
            }
        }
        viewModel.commentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, R.string.msg_comment_added, Snackbar.LENGTH_LONG).show()
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
