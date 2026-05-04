package com.uniandes.appmoviles.vinilos

import com.uniandes.appmoviles.vinilos.model.Comment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CommentModelTest {

    @Test
    fun `comment data class holds expected values`() {
        val comment = Comment(
            id = 1,
            description = "Great album",
            rating = 5,
            collectorId = 10,
            collectorName = "John Doe"
        )
        assertEquals(1, comment.id)
        assertEquals("Great album", comment.description)
        assertEquals(5, comment.rating)
        assertEquals(10, comment.collectorId)
        assertEquals("John Doe", comment.collectorName)
    }

    @Test
    fun `comment default collectorName is empty`() {
        val comment = Comment(id = 1, description = "Test", rating = 3)
        assertEquals("", comment.collectorName)
    }

    @Test
    fun `two comments with same data are equal`() {
        val c1 = Comment(id = 1, description = "Desc", rating = 4, collectorId = 1, collectorName = "Name")
        val c2 = Comment(id = 1, description = "Desc", rating = 4, collectorId = 1, collectorName = "Name")
        assertEquals(c1, c2)
    }

    @Test
    fun `two comments with different id are not equal`() {
        val c1 = Comment(id = 1, description = "Desc", rating = 4)
        val c2 = Comment(id = 2, description = "Desc", rating = 4)
        assertNotEquals(c1, c2)
    }

    @Test
    fun `can update collectorName`() {
        val comment = Comment(id = 1, description = "Desc", rating = 4)
        comment.collectorName = "New Name"
        assertEquals("New Name", comment.collectorName)
    }
}
