package com.twitter.twitterbelltest.cache

import android.util.LruCache
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Wrapper around Android's built in LruCache
 */
internal class LruCacheWrapper<Key : Any, Value : Any>(private val cache: LruCache<Key, Value>) :
    Cache<Key, Value> {
    constructor(maxSize: Int) : this(LruCache(maxSize))

    override fun remove(key: Key): Deferred<Unit> {
        return GlobalScope.async<Unit> {
            cache.remove(key)
        }
    }

    override fun get(key: Key): Deferred<Value?> {
        return GlobalScope.async {
            cache.get(key)
        }
    }

    override fun set(key: Key, value: Value): Deferred<Unit> {
        return GlobalScope.async<Unit> {
            cache.put(key, value)
        }
    }

    override fun removeAll(): Deferred<Unit> {
        return GlobalScope.async {
            cache.evictAll()
        }
    }
}

/**
 * Create a Cache from Android's built in LruCache
 * @property lruCache   An LruCache
 */
fun <Key : Any, Value : Any> Cache.Companion.fromLruCache(lruCache: LruCache<Key, Value>) = LruCacheWrapper(lruCache) as Cache<Key, Value>

/**
 * Create a Cache from a newly created LruCache
 * @property maxSize    Maximum number of entries
 */
fun <Key : Any, Value : Any> Cache.Companion.createLruCache(maxSize: Int) = Cache.fromLruCache(LruCache<Key, Value>(maxSize))
