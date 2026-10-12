package practice.refactored

import kotlin.math.ceil

/**
 * Service Layer (Use Case)
 *
 * Responsibilities:
 * - Coordinates document retrieval and audio generation.
 * - Enforces business rules (word count, reading time, default voice).
 * - Depends strictly on abstractions (DocumentRepository, AudioSynthesizer).
 */
class DocumentAudioService(
    private val documentRepository: DocumentRepository,
    private val audioSynthesizer: AudioSynthesizer,
    private val wordsPerMinute: Int = DEFAULT_WORDS_PER_MINUTE
) {

    companion object {
        const val DEFAULT_WORDS_PER_MINUTE = 200
        const val DEFAULT_VOICE = "GEORGE"
    }

    fun processAndSynthesize(docId: String, voiceType: String?): ProcessedAudioDocument {
        // 1. Fetch document via repository (which may be cached)
        val document = documentRepository.findById(docId)
            ?: throw IllegalArgumentException("Document could not be retrieved: $docId")

        // 2. Clean text
        val cleanedText = cleanText(document.content)

        // 3. Calculate metrics
        val wordCount = countWords(cleanedText)
        val readingTime = calculateReadingTimeMinutes(wordCount)

        // 4. Resolve voice
        val voice = resolveVoice(voiceType)

        // 5. Synthesize speech
        val audioBytes = audioSynthesizer.synthesize(cleanedText, voice)

        return ProcessedAudioDocument(
            id = docId,
            text = cleanedText,
            wordCount = wordCount,
            readingTimeMinutes = readingTime,
            voice = voice,
            audioBytes = audioBytes
        )
    }

    private fun cleanText(raw: String): String =
        raw.trim().replace("\\s+".toRegex(), " ")

    private fun countWords(text: String): Int =
        if (text.isBlank()) 0 else text.split(" ").count { it.isNotBlank() }

    private fun calculateReadingTimeMinutes(wordCount: Int): Int =
        if (wordCount > 0) ceil(wordCount.toDouble() / wordsPerMinute).toInt() else 0

    private fun resolveVoice(voiceType: String?): String =
        if (voiceType.isNullOrBlank()) DEFAULT_VOICE else voiceType.uppercase()
}
