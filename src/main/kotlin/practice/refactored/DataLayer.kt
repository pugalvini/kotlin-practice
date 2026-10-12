package practice.refactored

import practice.cache.Cache
import practice.legacy.AudioSynthesizerEngine
import practice.legacy.RemoteApiClient

/**
 * Data Layer: Interfaces & Implementations
 */

interface DocumentRepository {
    fun findById(docId: String): Document?
}

/**
 * Remote implementation talking to the API client
 */
class NetworkDocumentRepository(
    private val apiClient: RemoteApiClient
) : DocumentRepository {
    override fun findById(docId: String): Document? {
        return try {
            val raw = apiClient.fetchRawDocument(docId)
            Document(id = docId, content = raw)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Task 2 Implementation:
 * CachedDocumentRepository wraps DocumentRepository with the provided Cache (Decorator / Cache-Aside pattern).
 */
class CachedDocumentRepository(
    private val delegate: DocumentRepository,
    private val cache: Cache<String, Document>
) : DocumentRepository {

    override fun findById(docId: String): Document? {
        // 1. Check cache first
        val cached = cache.get(docId)
        if (cached != null) {
            return cached
        }

        // 2. Fetch from underlying delegate
        val fetched = delegate.findById(docId)

        // 3. Store in cache if found
        if (fetched != null) {
            cache.put(docId, fetched)
        }

        return fetched
    }
}

/**
 * Audio Synthesis Interface & Adapter
 */
interface AudioSynthesizer {
    fun synthesize(text: String, voice: String): ByteArray
}

class DefaultAudioSynthesizer(
    private val engine: AudioSynthesizerEngine
) : AudioSynthesizer {
    override fun synthesize(text: String, voice: String): ByteArray {
        return engine.synthesizeSpeech(text, voice)
    }
}
