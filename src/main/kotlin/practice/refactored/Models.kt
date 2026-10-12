package practice.refactored

/**
 * Domain Models & Value Objects
 * Replaces the untyped Map<String, Any> from the legacy code.
 */
data class Document(
    val id: String,
    val content: String
)

data class ProcessedAudioDocument(
    val id: String,
    val text: String,
    val wordCount: Int,
    val readingTimeMinutes: Int,
    val voice: String,
    val audioBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProcessedAudioDocument) return false
        return id == other.id && voice == other.voice && audioBytes.contentEquals(other.audioBytes)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + voice.hashCode()
        result = 31 * result + audioBytes.contentHashCode()
        return result
    }
}
