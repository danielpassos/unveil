package me.passos.libs.unveil.navigation

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NavigationStoreTest {

    private lateinit var store: NavigationStore

    @BeforeTest
    fun setUp() {
        store = NavigationStore()
    }

    @Test
    fun `stack and history are empty before any navigation`() {
        assertTrue(store.stack.isEmpty())
        assertTrue(store.history.isEmpty())
    }

    @Test
    fun `record updates the stack with the full stack`() {
        val fullStack = listOf(buildStackEntry("home"), buildStackEntry("profile"))
        store.record(buildEntry("profile"), fullStack)

        assertEquals(2, store.stack.size)
        assertEquals("home", store.stack[0].route)
        assertEquals("profile", store.stack[1].route)
    }

    @Test
    fun `record prepends the entry to history so newest appears first`() {
        store.record(buildEntry("home"), listOf(buildStackEntry("home")))
        store.record(buildEntry("profile"), listOf(buildStackEntry("home"), buildStackEntry("profile")))

        assertEquals("profile", store.history[0].route)
        assertEquals("home", store.history[1].route)
    }

    @Test
    fun `record replaces the entire stack on each navigation`() {
        store.record(buildEntry("home"), listOf(buildStackEntry("home")))
        store.record(buildEntry("profile"), listOf(buildStackEntry("home"), buildStackEntry("profile")))

        assertEquals(2, store.stack.size)

        store.record(buildEntry("home", NavigationDirection.Pop), listOf(buildStackEntry("home")))

        assertEquals(1, store.stack.size)
        assertEquals("home", store.stack[0].route)
    }

    @Test
    fun `history respects maxHistoryEntries cap`() {
        val store = NavigationStore(maxHistoryEntries = 3)
        store.record(buildEntry("a"), listOf(buildStackEntry("a")))
        store.record(buildEntry("b"), listOf(buildStackEntry("b")))
        store.record(buildEntry("c"), listOf(buildStackEntry("c")))
        store.record(buildEntry("d"), listOf(buildStackEntry("d")))

        assertEquals(3, store.history.size)
        assertEquals("d", store.history[0].route)
        assertEquals("c", store.history[1].route)
        assertEquals("b", store.history[2].route)
    }

    @Test
    fun `clearHistory empties history but preserves the stack`() {
        store.record(buildEntry("home"), listOf(buildStackEntry("home")))
        store.record(buildEntry("profile"), listOf(buildStackEntry("home"), buildStackEntry("profile")))

        store.clearHistory()

        assertTrue(store.history.isEmpty())
        assertEquals(2, store.stack.size)
    }

    @Test
    fun `maxHistoryEntries is exposed as a property`() {
        val store = NavigationStore(maxHistoryEntries = 25)

        assertEquals(25, store.maxHistoryEntries)
    }

    private fun buildEntry(
        route: String,
        direction: NavigationDirection = NavigationDirection.Push,
    ) = NavigationEntry(
        id = route,
        route = route,
        direction = direction,
        arguments = emptyMap(),
        timestamp = 0L,
    )

    private fun buildStackEntry(route: String) =
        StackEntry(route = route, arguments = emptyMap())
}
