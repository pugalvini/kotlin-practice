package practice.cache

/**
 * Provided LRU Cache Interface and Implementation.
 * DO NOT modify this file during the assessment (unless adding methods if allowed).
 */
interface Cache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K): V?
    fun size(): Int
    fun clear()
}

class LruCache<K, V>(private val capacity: Int) : Cache<K, V> {
    init {
        require(capacity > 0) { "Capacity must be greater than 0" }
    }

    // LinkedHashMap with accessOrder = true implements LRU ordering
    private val map = object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > capacity
        }
    }

    @Synchronized
    override fun get(key: K): V? = map[key]

    @Synchronized
    override fun put(key: K, value: V) {
        map[key] = value
    }

    @Synchronized
    override fun remove(key: K): V? = map.remove(key)

    @Synchronized
    override fun size(): Int = map.size

    @Synchronized
    override fun clear() = map.clear()
}
