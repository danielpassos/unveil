package me.passos.libs.unveil.logs

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogStoreTest {

    private lateinit var store: LogStore

    @BeforeTest
    fun setUp() {
        store = LogStore()
    }

    @Test
    fun `entries is empty before any logs are recorded`() {
        assertTrue(store.entries.isEmpty())
    }

    @Test
    fun `add inserts the entry into the store`() {
        store.add(buildEntry("id-1"))

        assertEquals(1, store.entries.size)
        assertEquals("id-1", store.entries[0].id)
    }

    @Test
    fun `add prepends entries so the newest appears first`() {
        store.add(buildEntry("id-1"))
        store.add(buildEntry("id-2"))

        assertEquals("id-2", store.entries[0].id)
        assertEquals("id-1", store.entries[1].id)
    }

    @Test
    fun `add discards the oldest entry when maxEntries is exceeded`() {
        val store = LogStore(maxEntries = 2)
        store.add(buildEntry("id-1"))
        store.add(buildEntry("id-2"))
        store.add(buildEntry("id-3"))

        assertEquals(2, store.entries.size)
        assertEquals("id-3", store.entries[0].id)
        assertEquals("id-2", store.entries[1].id)
    }

    @Test
    fun `store never exceeds maxEntries regardless of how many logs are added`() {
        val store = LogStore(maxEntries = 5)
        repeat(20) { store.add(buildEntry("id-$it")) }

        assertEquals(5, store.entries.size)
    }

    @Test
    fun `maxEntries is reflected in the store property`() {
        val store = LogStore(maxEntries = 50)

        assertEquals(50, store.maxEntries)
    }

    @Test
    fun `clear removes all entries`() {
        store.add(buildEntry("id-1"))
        store.add(buildEntry("id-2"))

        store.clear()

        assertTrue(store.entries.isEmpty())
    }

    private fun buildEntry(id: String) =
        LogEntry(
            id = id,
            level = LogLevel.Debug,
            tag = "Test",
            message = "test message",
            timestamp = 0L,
        )
}
