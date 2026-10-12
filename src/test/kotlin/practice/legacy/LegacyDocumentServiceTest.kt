package practice.legacy

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LegacyDocumentServiceTest {

    private val service = LegacyDocumentService()

    @Test
    fun `successfully processes and synthesizes existing document with default voice`() {
        val result = service.processAndSynthesizeDocument("doc-1", null)

        assertEquals("doc-1", result["id"])
        assertEquals("Speechify helps you read faster. Listening to audiobooks improves retention.", result["text"])
        assertEquals(10, result["wordCount"])
        assertEquals(1, result["readingTimeMinutes"])
        assertEquals("GEORGE", result["voice"])
        assertNotNull(result["audioBytes"])
    }

    @Test
    fun `uses custom uppercase voice when provided`() {
        val result = service.processAndSynthesizeDocument("doc-2", "kendra")

        assertEquals("KENDRA", result["voice"])
        assertEquals("doc-2", result["id"])
    }

    @Test
    fun `throws exception for non existent document`() {
        assertThrows<IllegalArgumentException> {
            service.processAndSynthesizeDocument("doc-unknown", "GEORGE")
        }
    }

    @Test
    fun `tracks call counts without caching`() {
        service.processAndSynthesizeDocument("doc-1", "GEORGE")
        service.processAndSynthesizeDocument("doc-1", "GEORGE")

        // In legacy code, remote API is invoked twice because there is no cache!
        assertEquals(2, service.apiClient.callCount)
        assertEquals(2, service.audioEngine.synthesisCount)
    }
}
