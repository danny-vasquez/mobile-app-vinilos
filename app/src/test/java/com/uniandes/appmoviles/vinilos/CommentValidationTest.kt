package com.uniandes.appmoviles.vinilos

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommentValidationTest {

    // Replica la condición de validación de AlbumDetailFragment.showAddCommentDialog()
    private fun isCommentValid(
        name: String,
        telephone: String,
        email: String,
        description: String,
        rating: Int
    ): Boolean = name.isNotBlank()
            && telephone.isNotBlank()
            && email.isNotBlank()
            && description.isNotBlank()
            && rating > 0

    // ─── Flujo válido ────────────────────────────────────────────────────────

    @Test
    fun `all fields filled with rating 1 is valid`() {
        assertTrue(isCommentValid("Juan", "3001234567", "j@e.com", "Excelente", 1))
    }

    @Test
    fun `all fields filled with rating 5 is valid`() {
        assertTrue(isCommentValid("Juan", "3001234567", "j@e.com", "Excelente", 5))
    }

    // ─── Validación de nombre ─────────────────────────────────────────────────

    @Test
    fun `empty name is invalid`() {
        assertFalse(isCommentValid("", "3001234567", "j@e.com", "Excelente", 5))
    }

    @Test
    fun `blank name is invalid`() {
        assertFalse(isCommentValid("   ", "3001234567", "j@e.com", "Excelente", 5))
    }

    // ─── Validación de teléfono ───────────────────────────────────────────────

    @Test
    fun `empty telephone is invalid`() {
        assertFalse(isCommentValid("Juan", "", "j@e.com", "Excelente", 5))
    }

    @Test
    fun `blank telephone is invalid`() {
        assertFalse(isCommentValid("Juan", "   ", "j@e.com", "Excelente", 5))
    }

    // ─── Validación de email ──────────────────────────────────────────────────

    @Test
    fun `empty email is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "", "Excelente", 5))
    }

    @Test
    fun `blank email is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "   ", "Excelente", 5))
    }

    // ─── Validación de descripción ────────────────────────────────────────────

    @Test
    fun `empty description is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "j@e.com", "", 5))
    }

    @Test
    fun `blank description is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "j@e.com", "   ", 5))
    }

    // ─── Validación de rating ─────────────────────────────────────────────────

    @Test
    fun `rating zero is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "j@e.com", "Excelente", 0))
    }

    @Test
    fun `negative rating is invalid`() {
        assertFalse(isCommentValid("Juan", "3001234567", "j@e.com", "Excelente", -1))
    }

    // ─── Todos los campos vacíos ──────────────────────────────────────────────

    @Test
    fun `all fields empty is invalid`() {
        assertFalse(isCommentValid("", "", "", "", 0))
    }
}
