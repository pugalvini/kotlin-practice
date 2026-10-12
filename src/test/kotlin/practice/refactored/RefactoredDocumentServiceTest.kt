package practice.refactored

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import practice.cache.LruCache
import practice.legacy.AudioSynthesizerEngine
import practice.legacy.RemoteApiClient

class RefactoredDocumentServiceTest {

    private lateinit var apiClient: RemoteApiClient
    private lateinit var audioEngine: AudioSynthesizerEngine
    private lateinit var networkRepo: NetworkDocumentRepository
    private lateinit var cache: LruCache<String, Document>
    private lateinit var cachedRepo: CachedDocumentRepository
    private lateinit var service: DocumentAudioService

    @BeforeEach
    fun setUp() {
        apiClient = RemoteApiClient()
        audioEngine = AudioSynthesizerEngine()
        networkRepo = NetworkDocumentRepository(apiClient)
        // Cache capacity of 2 items
        cache = LruCache(capacity = 2)
        cachedRepo = CachedDocumentRepository(delegate = networkRepo, cache = cache)
        service = DocumentAudioService(
            documentRepository = cachedRepo,
            audioSynthesizer = DefaultAudioSynthesizer(audioEngine)
        )
    }

    @Test
    fun `successfully processes document matching legacy expectations`() {
        val result = service.processAndSynthesize("doc-1", null)

        assertEquals("doc-1", result.id)
        assertEquals("Speechify helps you read faster. Listening to audiobooks improves retention.", result.text)
        assertEquals(10, result.wordCount)
        assertEquals(1, result.readingTimeMinutes)
        assertEquals("GEORGE", result.voice)
        assertTrue(result.audioBytes.isNotEmpty())
    }

    @Test
    fun `throws exception for missing document`() {
        val ex = assertThrows<IllegalArgumentException> {
            service.processAndSynthesize("doc-unknown", "GEORGE")
        }
        assertTrue(ex.message!!.contains("doc-unknown"))
    }

    @Test
    fun `LRU Cache prevents duplicate network calls on cache hit`() {
        // First access -> Cache Miss -> network API call count = 1
        service.processAndSynthesize("doc-1", "GEORGE")
        assertEquals(1, apiClient.callCount)
        assertEquals(1, cache.size())

        // Second access -> Cache Hit -> network API call count remains 1!
        service.processAndSynthesize("doc-1", "GEORGE")
        assertEquals(1, apiClient.callCount)
    }

    @Test
    fun `LRU Cache evicts least recently accessed item when capacity is exceeded`() {
        // Capacity is 2
        service.processAndSynthesize("doc-1", "GEORGE") // doc-1 stored
        service.processAndSynthesize("doc-2", "GEORGE") // doc-2 stored
        assertEquals(2, cache.size())
        assertEquals(2, apiClient.callCount)

        // Access doc-1 again to make doc-2 the least recently used
        cachedRepo.findById("doc-1")

        // Store a 3rd document manually in repo or mock to test eviction
        cache.put("doc-3", Document("doc-3", "Content 3"))

        // Since capacity was 2, doc-2 should have been evicted!
        assertNotNull(cache.get("doc-1"))
        assertNotNull(cache.get("doc-3"))
        assertNull(cache.get("doc-2"))
    }
}
