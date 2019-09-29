package com.twitter.twitterbelltest.cache

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LruCacheWrapperWithCache {

    private lateinit var integratedCache: Cache<String, String>

    @Before
    fun before() {
        integratedCache = Cache.createLruCache(10)
    }

    @Test
    fun testReturnNullWhenCacheEmpty() {
        runBlocking {
            // given we have an empty cache, integratedCache

            // when we retrieve a value
            val result = integratedCache.get("key").await()

            // then it is null
            assertNull(result)
        }
    }

    @Test
    fun testReturnResultWhenCacheIsNotEmpty() {
        runBlocking {
            // given we have a cache with a value
            integratedCache.set("key", "value").await()

            // when we retrieve a value
            val result = integratedCache.get("key").await()

            // then it is returned
            assertEquals("value", result)
        }
    }

    @Test
    fun testReturnNullWhenKeyRemoved() {
        runBlocking {
            // given we have a cache with a value
            integratedCache.set("key", "value").await()

            // when we evict the value
            integratedCache.remove("key").await()

            // then the value is null
            assertNull(integratedCache.get("key").await())
        }
    }

    @Test
    fun testRemoveFirstValueWhenCacheSizeIs1() {
        runBlocking {
            // given we create a cache of size 1
            val cache = LruCacheWrapper<String, String>(1)

            // when we set 2 values
            cache.set("key1", "value1").await()
            cache.set("key2", "value2").await()

            // then only the second value is available
            assertNull(cache.get("key1").await())
            assertEquals("value2", cache.get("key2").await())
        }
    }

    @Test
    fun testRemoveOldestNotUsedValue() {
        runBlocking {
            // given we create and populate a cache of size 2
            val cache = LruCacheWrapper<String, String>(2)
            cache.set("key1", "value1").await()
            cache.set("key2", "value2").await()

            // when we get the 1st and add a 3rd value
            cache.get("key1").await()
            cache.set("key3", "value3").await()

            // then the 2nd is removed leaving the 1st and 3rd values
            assertNull(cache.get("key2").await())
            assertEquals("value1", cache.get("key1").await())
            assertEquals("value3", cache.get("key3").await())
        }
    }
}
