package practice.legacy

/**
 * STARTER CODE (MESSY CODE TO REFACTOR)
 *
 * Typical Code Smells:
 * 1. God Object / Violates Single Responsibility: Fetches remote content, cleans text,
 *    calculates metrics, and requests audio speech generation all in one class.
 * 2. Tight Coupling: Directly instantiates RemoteApiClient and AudioSynthesizerEngine.
 * 3. Primitive Obsession & Magic Numbers: 200 words per minute hardcoded, status strings.
 * 4. Mutable state & lack of interfaces: Hard to test or substitute.
 * 5. No caching: Repeated requests fetch from network and re-synthesize audio every time.
 */

// Simulates external third-party SDKs
class RemoteApiClient {
    var callCount = 0
        private set

    fun fetchRawDocument(docId: String): String {
        callCount++
        // Simulates network latency/call
        return when (docId) {
            "doc-1" -> "Speechify helps you read faster. Listening to audiobooks improves retention."
            "doc-2" -> "Kotlin is a modern programming language on the JVM with null safety."
            else -> throw RuntimeException("404 Document Not Found: $docId")
        }
    }
}

class AudioSynthesizerEngine {
    var synthesisCount = 0
        private set

    fun synthesizeSpeech(text: String, voice: String): ByteArray {
        synthesisCount++
        // Simulates audio byte stream generation
        return "AUDIO_STREAM[voice=$voice, len=${text.length}]".toByteArray()
    }
}

class LegacyDocumentService {
    // Tight coupling: instantiated directly
    val apiClient = RemoteApiClient()
    val audioEngine = AudioSynthesizerEngine()

    // Public mutable tracking
    var processedCount = 0

    fun processAndSynthesizeDocument(docId: String, voiceType: String?): Map<String, Any> {
        println("Processing document $docId with voice $voiceType")

        // 1. Fetch remote content
        val rawText: String
        try {
            rawText = apiClient.fetchRawDocument(docId)
        } catch (e: Exception) {
            println("Error fetching document: ${e.message}")
            throw IllegalArgumentException("Document could not be retrieved")
        }

        // 2. Text cleaning & normalization
        val cleanedText = rawText.trim().replace("\\s+".toRegex(), " ")

        // 3. Word counting & Reading time estimation (Assuming average 200 WPM)
        val words = cleanedText.split(" ").filter { it.isNotBlank() }
        val wordCount = words.size
        val estimatedReadingMinutes = if (wordCount > 0) Math.ceil(wordCount / 200.0).toInt() else 0

        // 4. Voice resolution (default to "GEORGE" if null or empty)
        val selectedVoice = if (voiceType.isNullOrBlank()) {
            "GEORGE"
        } else {
            voiceType.uppercase()
        }

        // 5. Audio synthesis
        val audioBytes = audioEngine.synthesizeSpeech(cleanedText, selectedVoice)

        processedCount++

        // 6. Untyped Map return (Lack of domain model)
        val result = mutableMapOf<String, Any>()
        result["id"] = docId
        result["text"] = cleanedText
        result["wordCount"] = wordCount
        result["readingTimeMinutes"] = estimatedReadingMinutes
        result["voice"] = selectedVoice
        result["audioBytes"] = audioBytes

        return result
    }
}
