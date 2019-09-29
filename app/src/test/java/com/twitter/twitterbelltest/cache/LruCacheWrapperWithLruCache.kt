package com.twitter.twitterbelltest.cache

import android.util.LruCache
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LruCacheWrapperWithLruCache {

    @Mock
    private lateinit var lruCache: LruCache<String, String>

    private lateinit var wrappedCache: Cache<String, String>

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        wrappedCache = LruCacheWrapper(lruCache)
    }

    // get
    @Test
    fun testGetValueFromAndroidLruCache() {
        runBlocking {
            // given value available in first cache only
            Mockito.`when`(lruCache.get("key")).thenReturn("value")

            // when we get the value
            val result = wrappedCache.get("key").await()

            // then we return the value
            Assert.assertEquals("value", result)
        }
    }

    @Test(expected = Exception::class)
    fun testThrowExceptionDuringGet() {
        runBlocking {
            // given value available in first cache only
            Mockito.`when`(lruCache.get("key")).then { throw Exception() }

            // when we get the value it will throw exception
            wrappedCache.get("key").await()

        }
    }

    // set
    @Test
    fun testSetValueToAndroiLruCache() {
        runBlocking {
            // when we set the value
            wrappedCache.set("key", "value").await()

            // then put is called
            Mockito.verify(lruCache).put("key", "value")
        }
    }

    @Test(expected = Exception::class)
    fun testSetExceptionDuringPut() {
        runBlocking {
            // given value available in first cache only
            Mockito.`when`(lruCache.put("key", "value")).then { throw Exception() }

            // when we set the value throw exception
            wrappedCache.set("key", "value").await()
        }
    }

    // evict
    @Test
    fun testRemoveValueFromLruCache() {
        runBlocking {
            // when we get the value
            wrappedCache.remove("key").await()

            Mockito.verify(lruCache).remove("key")
        }
    }

    @Test(expected = Exception::class)
    fun testThrowExceptionDuringRemove() {
        runBlocking {
            // given value available in first cache only
            Mockito.`when`(lruCache.remove("key")).then { throw Exception() }

            // when we remove the value throw Exception
            wrappedCache.remove("key").await()
        }
    }
}
