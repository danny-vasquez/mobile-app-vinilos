package com.uniandes.appmoviles.vinilos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.uniandes.appmoviles.vinilos.model.Album
import com.uniandes.appmoviles.vinilos.model.AlbumRequest
import com.uniandes.appmoviles.vinilos.repository.interfaces.AlbumRepository
import com.uniandes.appmoviles.vinilos.ui.albums.CreateAlbumUiState
import com.uniandes.appmoviles.vinilos.ui.albums.CreateAlbumViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CreateAlbumViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: AlbumRepository
    private lateinit var viewModel: CreateAlbumViewModel

    private val validAlbum = Album(
        albumId = 1,
        name = "Abbey Road",
        cover = "https://example.com/cover.jpg",
        releaseDate = "1969-09-26",
        description = "Iconic album by The Beatles",
        genre = "Rock",
        recordLabel = "EMI"
    )

    @Before
    fun setUp() {
        mockRepository = mock()
        viewModel = CreateAlbumViewModel(mockRepository)
    }

    @Test
    fun `initial state is Idle`() {
        assertTrue(viewModel.uiState.value is CreateAlbumUiState.Idle)
    }

    @Test
    fun `createAlbum with valid data transitions to Success`() {
        whenever(mockRepository.createAlbum(any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onComplete = invocation.arguments[1] as (Album) -> Unit
            onComplete(validAlbum)
        }

        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Iconic album by The Beatles",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue("Expected Success but was $state", state is CreateAlbumUiState.Success)
        assertEquals(validAlbum, (state as CreateAlbumUiState.Success).album)
    }

    @Test
    fun `createAlbum with empty name results in Error on name field`() {
        viewModel.createAlbum(
            name = "",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is CreateAlbumUiState.Error)
        assertEquals("name", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with blank name results in Error`() {
        viewModel.createAlbum(
            name = "   ",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("name", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with empty cover results in Error on cover field`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("cover", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with invalid cover URL not starting with http results in Error`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "ftp://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is CreateAlbumUiState.Error)
        assertEquals("cover", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with plain string as cover URL results in Error`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "not-a-url",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("cover", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with https cover URL is accepted`() {
        whenever(mockRepository.createAlbum(any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onComplete = invocation.arguments[1] as (Album) -> Unit
            onComplete(validAlbum)
        }

        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        assertTrue(viewModel.uiState.value is CreateAlbumUiState.Success)
    }

    @Test
    fun `createAlbum with empty releaseDate results in Error on releaseDate field`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("releaseDate", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with empty description results in Error on description field`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("description", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with empty genre results in Error on genre field`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is CreateAlbumUiState.Error)
        assertEquals("genre", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum with empty recordLabel results in Error on recordLabel field`() {
        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = ""
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertEquals("recordLabel", (state as CreateAlbumUiState.Error).field)
    }

    @Test
    fun `createAlbum API error transitions to Error state without field`() {
        whenever(mockRepository.createAlbum(any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onError = invocation.arguments[2] as (Exception) -> Unit
            onError(Exception("Sin conexión a internet"))
        }

        viewModel.createAlbum(
            name = "Abbey Road",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1969-09-26",
            description = "Some description",
            genre = "Rock",
            recordLabel = "EMI"
        )

        val state = viewModel.uiState.value
        assertTrue(state is CreateAlbumUiState.Error)
        assertNull((state as CreateAlbumUiState.Error).field)
        assertEquals("Sin conexión a internet", state.message)
    }

    @Test
    fun `resetState transitions back to Idle`() {
        viewModel.createAlbum("", "", "", "", "", "")
        assertTrue(viewModel.uiState.value is CreateAlbumUiState.Error)

        viewModel.resetState()
        assertTrue(viewModel.uiState.value is CreateAlbumUiState.Idle)
    }
}
