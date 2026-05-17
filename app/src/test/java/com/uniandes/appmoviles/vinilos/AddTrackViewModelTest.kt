package com.uniandes.appmoviles.vinilos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.uniandes.appmoviles.vinilos.model.Track
import com.uniandes.appmoviles.vinilos.model.TrackRequest
import com.uniandes.appmoviles.vinilos.repository.interfaces.AlbumRepository
import com.uniandes.appmoviles.vinilos.ui.albums.AddTrackUiState
import com.uniandes.appmoviles.vinilos.ui.albums.AddTrackViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AddTrackViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: AlbumRepository
    private lateinit var viewModel: AddTrackViewModel

    private val albumId = 1
    private val validTrack = Track(id = 10, name = "Come Together", duration = "4:19")

    @Before
    fun setUp() {
        mockRepository = mock()
        viewModel = AddTrackViewModel(albumId, mockRepository)
    }

    @Test
    fun `initial state is Idle`() {
        assertTrue(viewModel.uiState.value is AddTrackUiState.Idle)
    }

    @Test
    fun `addTrack with valid name and MM_SS duration transitions to Success`() {
        whenever(mockRepository.addTrack(any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onSuccess = invocation.arguments[2] as (Track) -> Unit
            onSuccess(validTrack)
        }

        viewModel.addTrack(name = "Come Together", duration = "4:19")

        val state = viewModel.uiState.value
        assertTrue("Expected Success but was $state", state is AddTrackUiState.Success)
        assertEquals(validTrack, (state as AddTrackUiState.Success).track)
    }

    @Test
    fun `addTrack with valid HH_MM_SS duration transitions to Success`() {
        val longTrack = Track(id = 11, name = "Thick as a Brick", duration = "1:02:30")
        whenever(mockRepository.addTrack(any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onSuccess = invocation.arguments[2] as (Track) -> Unit
            onSuccess(longTrack)
        }

        viewModel.addTrack(name = "Thick as a Brick", duration = "1:02:30")

        val state = viewModel.uiState.value
        assertTrue("Expected Success but was $state", state is AddTrackUiState.Success)
        assertEquals(longTrack, (state as AddTrackUiState.Success).track)
    }

    @Test
    fun `addTrack with empty name results in Error on name field`() {
        viewModel.addTrack(name = "", duration = "3:45")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddTrackUiState.Error)
        assertEquals("name", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack with blank name results in Error on name field`() {
        viewModel.addTrack(name = "   ", duration = "3:45")

        val state = viewModel.uiState.value
        assertTrue(state is AddTrackUiState.Error)
        assertEquals("name", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack with empty duration results in Error on duration field`() {
        viewModel.addTrack(name = "Come Together", duration = "")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddTrackUiState.Error)
        assertEquals("duration", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack with alphabetic duration results in Error on duration field`() {
        viewModel.addTrack(name = "Come Together", duration = "abc")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddTrackUiState.Error)
        assertEquals("duration", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack with dash-separated duration results in Error on duration field`() {
        viewModel.addTrack(name = "Come Together", duration = "3-45")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddTrackUiState.Error)
        assertEquals("duration", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack with missing seconds part results in Error on duration field`() {
        viewModel.addTrack(name = "Come Together", duration = "345")

        val state = viewModel.uiState.value
        assertTrue(state is AddTrackUiState.Error)
        assertEquals("duration", (state as AddTrackUiState.Error).field)
    }

    @Test
    fun `addTrack API error transitions to Error state without field`() {
        whenever(mockRepository.addTrack(any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onError = invocation.arguments[3] as (Exception) -> Unit
            onError(Exception("Sin conexión a internet"))
        }

        viewModel.addTrack(name = "Come Together", duration = "4:19")

        val state = viewModel.uiState.value
        assertTrue(state is AddTrackUiState.Error)
        val error = state as AddTrackUiState.Error
        assertEquals("Sin conexión a internet", error.message)
        assertEquals(null, error.field)
    }

    @Test
    fun `resetState transitions back to Idle`() {
        viewModel.addTrack(name = "", duration = "")
        assertTrue(viewModel.uiState.value is AddTrackUiState.Error)

        viewModel.resetState()
        assertTrue(viewModel.uiState.value is AddTrackUiState.Idle)
    }
}
