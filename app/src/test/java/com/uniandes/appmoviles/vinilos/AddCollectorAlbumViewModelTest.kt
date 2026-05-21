package com.uniandes.appmoviles.vinilos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.uniandes.appmoviles.vinilos.model.Collector
import com.uniandes.appmoviles.vinilos.repository.interfaces.CollectorRepository
import com.uniandes.appmoviles.vinilos.ui.albums.AddCollectorAlbumUiState
import com.uniandes.appmoviles.vinilos.ui.albums.AddCollectorAlbumViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AddCollectorAlbumViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: CollectorRepository

    private val albumId = 1
    private val collectors = listOf(
        Collector(id = 1, name = "Manolo Bellon"),
        Collector(id = 2, name = "Pepito Pérez")
    )

    @Before
    fun setUp() {
        mockRepository = mock()
    }

    private fun createViewModel(): AddCollectorAlbumViewModel {
        whenever(mockRepository.getCollectors(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onComplete = invocation.arguments[0] as (List<Collector>) -> Unit
            onComplete(collectors)
        }
        return AddCollectorAlbumViewModel(albumId, mockRepository)
    }

    private fun createViewModelStalled(): AddCollectorAlbumViewModel {
        // No callback triggered → stays in LoadingCollectors
        return AddCollectorAlbumViewModel(albumId, mockRepository)
    }

    // ─── Estado inicial ───────────────────────────────────────────────────────

    @Test
    fun `initial state is LoadingCollectors`() {
        val viewModel = createViewModelStalled()
        assertTrue(viewModel.uiState.value is AddCollectorAlbumUiState.LoadingCollectors)
    }

    @Test
    fun `after collectors load state transitions to Ready with the collector list`() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue("Expected Ready but was $state", state is AddCollectorAlbumUiState.Ready)
        assertEquals(collectors, (state as AddCollectorAlbumUiState.Ready).collectors)
    }

    @Test
    fun `load collectors error transitions to Error state`() {
        whenever(mockRepository.getCollectors(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onError = invocation.arguments[1] as (Exception) -> Unit
            onError(Exception("Error al cargar coleccionistas"))
        }
        val viewModel = AddCollectorAlbumViewModel(albumId, mockRepository)

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddCollectorAlbumUiState.Error)
        assertEquals("Error al cargar coleccionistas", (state as AddCollectorAlbumUiState.Error).message)
    }

    // ─── Flujo exitoso ────────────────────────────────────────────────────────

    @Test
    fun `addAlbumToCollector with valid data transitions to Success`() {
        val viewModel = createViewModel()
        whenever(mockRepository.addAlbumToCollector(any(), any(), any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onComplete = invocation.arguments[4] as () -> Unit
            onComplete()
        }

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "25000", status = "Active")

        assertTrue(viewModel.uiState.value is AddCollectorAlbumUiState.Success)
    }

    @Test
    fun `addAlbumToCollector with decimal price is accepted`() {
        val viewModel = createViewModel()
        whenever(mockRepository.addAlbumToCollector(any(), any(), any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onComplete = invocation.arguments[4] as () -> Unit
            onComplete()
        }

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "25000.50", status = "Inactive")

        assertTrue(viewModel.uiState.value is AddCollectorAlbumUiState.Success)
    }

    // ─── Validación de coleccionista ──────────────────────────────────────────

    @Test
    fun `addAlbumToCollector with collectorId minus one results in Error on collector field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = -1, priceText = "25000", status = "Active")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddCollectorAlbumUiState.Error)
        assertEquals("collector", (state as AddCollectorAlbumUiState.Error).field)
    }

    @Test
    fun `addAlbumToCollector with collectorId zero results in Error on collector field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 0, priceText = "25000", status = "Active")

        val state = viewModel.uiState.value
        assertTrue(state is AddCollectorAlbumUiState.Error)
        assertEquals("collector", (state as AddCollectorAlbumUiState.Error).field)
    }

    // ─── Validación de precio ─────────────────────────────────────────────────

    @Test
    fun `addAlbumToCollector with empty price results in Error on price field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "", status = "Active")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddCollectorAlbumUiState.Error)
        assertEquals("price", (state as AddCollectorAlbumUiState.Error).field)
    }

    @Test
    fun `addAlbumToCollector with blank price results in Error on price field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "   ", status = "Active")

        val state = viewModel.uiState.value
        assertTrue(state is AddCollectorAlbumUiState.Error)
        assertEquals("price", (state as AddCollectorAlbumUiState.Error).field)
    }

    @Test
    fun `addAlbumToCollector with alphabetic price results in Error on price field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "veinte", status = "Active")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddCollectorAlbumUiState.Error)
        assertEquals("price", (state as AddCollectorAlbumUiState.Error).field)
    }

    // ─── Validación de estado ─────────────────────────────────────────────────

    @Test
    fun `addAlbumToCollector with empty status results in Error on status field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "25000", status = "")

        val state = viewModel.uiState.value
        assertTrue("Expected Error but was $state", state is AddCollectorAlbumUiState.Error)
        assertEquals("status", (state as AddCollectorAlbumUiState.Error).field)
    }

    @Test
    fun `addAlbumToCollector with blank status results in Error on status field`() {
        val viewModel = createViewModel()

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "25000", status = "   ")

        val state = viewModel.uiState.value
        assertTrue(state is AddCollectorAlbumUiState.Error)
        assertEquals("status", (state as AddCollectorAlbumUiState.Error).field)
    }

    // ─── Error de red ─────────────────────────────────────────────────────────

    @Test
    fun `addAlbumToCollector API error transitions to Error state without field`() {
        val viewModel = createViewModel()
        whenever(mockRepository.addAlbumToCollector(any(), any(), any(), any(), any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val onError = invocation.arguments[5] as (Exception) -> Unit
            onError(Exception("Sin conexión a internet"))
        }

        viewModel.addAlbumToCollector(collectorId = 1, priceText = "25000", status = "Active")

        val state = viewModel.uiState.value
        assertTrue(state is AddCollectorAlbumUiState.Error)
        val error = state as AddCollectorAlbumUiState.Error
        assertEquals("Sin conexión a internet", error.message)
        assertNull(error.field)
    }
}
