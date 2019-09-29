package com.twitter.twitterbelltest.cache

import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SharedPreferencesWithCache {

    private lateinit var stringCache: Cache<String, String>
    private lateinit var intCache: Cache<String, Int>

    @Before
    fun before() {
        stringCache = SharedPreferencesCache(ApplicationProvider.getApplicationContext(), "test").withString()
        intCache = SharedPreferencesCache(ApplicationProvider.getApplicationContext(), "test").withInt()
    }

    @After
    fun after() {
        runBlocking {
            stringCache.removeAll().await()
            intCache.removeAll().await()
        }
    }

    @Test
    fun testReturnValueStringFromPreferenceCache() {
        runBlocking {
            // given we have a cache with a value
            stringCache.set("key", "value").await()

            // when we retrieve a value
            val result = stringCache.get("key").await()

            // then it is returned
            assertEquals("value", result)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testReturnExceptionWhenSetNull() {
        runBlocking {
            val cache = SharedPreferencesCache(ApplicationProvider.getApplicationContext(), "test").withString()

            // when set null argument throws exception
            cache.set("key", uninitialized()).await()

        }
    }

    @Test
    fun testReturnValueIntFromPreferenceCache() {
        runBlocking {
            val cache = SharedPreferencesCache(ApplicationProvider.getApplicationContext(), "test").withInt()

            // given we have a cache with a value
            cache.set("key", 5).await()

            // when we retrieve a value
            val result = cache.get("key").await()

            // then it is returned
            assertEquals(5, result)
        }
    }


    @Test
    fun testReturnNullWhenEmptyCache(){
        runBlocking {
            // when we retrieve a value but cache is empty
            val result = stringCache.get("key").await()

            // then it is null
            assertNull(result)
        }
    }

    @Test
    fun testReturnNullWhenValueIsBeingRemoved() {
        runBlocking {
            // given we have a cache with a value
            stringCache.set("key", "value").await()

            // when we evict the value
            stringCache.remove("key").await()

            // then the value is null
            assertNull(stringCache.get("key").await())
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T = null as T
    }
}
