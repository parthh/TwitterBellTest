package com.twitter.twitterbelltest.cache

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * A standard cache which stores and retrieves data
 */
interface Cache<Key : Any, Value : Any> {
    /**
     * Companion object for 'static' extension functions
     */
    companion object Companion

    /**
     * Return the value associated with the key or null if not present
     */
    fun get(key: Key): Deferred<Value?>

    /**
     * Save the value against the key
     */
    fun set(key: Key, value: Value): Deferred<Unit>

    /**
     * Remove the data associated with the key
     */
    fun remove(key: Key): Deferred<Unit>

    /**
     * Remove the data associated with all keys
     */
    fun removeAll(): Deferred<Unit>

    /**
     * Return data associated with multiple keys.
     */
    fun batchGet(keys: List<Key>): Deferred<List<Value?>> {
        keys.requireNoNulls()

        return GlobalScope.async {
            keys.map { this@Cache.get(it) }.map { it.await() }
        }
    }

    /**
     * Set data for multiple key/value pairs
     */
    fun batchSet(values: Map<Key, Value>): Deferred<Unit> {
        values.keys.requireNoNulls()

        return GlobalScope.async {
            values.map { entry: Map.Entry<Key, Value> ->
                this@Cache.set(entry.key, entry.value)
            }.forEach { it.await() }
            Unit
        }
    }
}
