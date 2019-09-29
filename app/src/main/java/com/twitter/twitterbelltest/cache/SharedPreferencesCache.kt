package com.twitter.twitterbelltest.cache

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * Simple cache that stores values associated with keys in a shared preferences file with no cleanup logic
 */
class SharedPreferencesCache(context: Context, preferenceFileKey: String) {

    private val sharedPreferences = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE)

    private class BaseCache<T : Any>(private val sharedPreferences: SharedPreferences,
                                     val getFun: (sharedPreferences: SharedPreferences, key: String) -> T?,
                                     val setFun: (editor: SharedPreferences.Editor, key: String, value: T) -> Unit) :
            Cache<String, T> {

        override fun get(key: String): Deferred<T?> {
            return GlobalScope.async {
                if (sharedPreferences.contains(key)) {
                    getFun(sharedPreferences, key)
                } else {
                    null
                }
            }
        }

        override fun set(key: String, value: T): Deferred<Unit> {
            return GlobalScope.async {
                val editor = sharedPreferences.edit()
                setFun(editor, key, value)
                editor.apply()
            }
        }

        override fun remove(key: String): Deferred<Unit> {
            return GlobalScope.async {
                val editor = sharedPreferences.edit()
                editor.remove(key)
                editor.apply()
            }
        }

        override fun removeAll(): Deferred<Unit> {
            return GlobalScope.async {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
            }
        }
    }

    /**
     * String value shared preference cache
     */
    fun withString(): Cache<String, String> {
        return BaseCache(sharedPreferences,
                { sharedPreferences, key -> sharedPreferences.getString(key, null) },
                { editor, key, value -> editor.putString(key, value) })
    }

    /**
     * Int value shared preference cache
     */
    fun withInt(): Cache<String, Int> {
        return BaseCache(sharedPreferences,
                { sharedPreferences: SharedPreferences, key: String -> sharedPreferences.getInt(key, 0) },
                { editor, key, value -> editor.putInt(key, value) })
    }

    /**
     * Float value shared preference cache
     */
    fun withFloat(): Cache<String, Float> {
        return BaseCache(sharedPreferences,
                { sharedPreferences, key -> sharedPreferences.getFloat(key, 0f) },
                { editor, key, value -> editor.putFloat(key, value) })
    }

    /**
     * Boolean value shared preference cache
     */
    fun withBoolean(): Cache<String, Boolean> {
        return BaseCache(sharedPreferences,
                { sharedPreferences, key -> sharedPreferences.getBoolean(key, false) },
                { editor, key, value -> editor.putBoolean(key, value) })
    }

    /**
     * Long value shared preference cache
     */
    fun withLong(): Cache<String, Long> {
        return BaseCache(sharedPreferences,
                { sharedPreferences, key -> sharedPreferences.getLong(key, 0) },
                { editor, key, value -> editor.putLong(key, value) })
    }
}
