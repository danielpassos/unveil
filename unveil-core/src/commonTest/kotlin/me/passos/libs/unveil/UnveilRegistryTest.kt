package me.passos.libs.unveil

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UnveilRegistryTest {

    @BeforeTest
    fun setUp() {
        UnveilRegistry.clearAll()
    }

    @AfterTest
    fun tearDown() {
        UnveilRegistry.clearAll()
    }

    @Test
    fun `plugins is empty when no plugins are registered`() {
        assertTrue(UnveilRegistry.plugins.isEmpty())
    }

    @Test
    fun `register adds a plugin`() {
        val plugin = FakeUnveilPlugin(id = "a")

        UnveilRegistry.register(plugin)

        assertEquals(1, UnveilRegistry.plugins.size)
        assertSame(plugin, UnveilRegistry.plugins[0])
    }

    @Test
    fun `register two distinct plugins preserves insertion order`() {
        val first = FakeUnveilPlugin(id = "first")
        val second = FakeUnveilPlugin(id = "second")

        UnveilRegistry.register(first)
        UnveilRegistry.register(second)

        assertEquals(listOf(first, second), UnveilRegistry.plugins)
    }

    @Test
    fun `register with duplicate id replaces the existing plugin`() {
        val original = FakeUnveilPlugin(id = "dup", title = "Original")
        val replacement = FakeUnveilPlugin(id = "dup", title = "Replacement")

        UnveilRegistry.register(original)
        UnveilRegistry.register(replacement)

        assertEquals(1, UnveilRegistry.plugins.size)
        assertSame(replacement, UnveilRegistry.plugins[0])
    }

    @Test
    fun `register with duplicate id preserves position in the list`() {
        val first = FakeUnveilPlugin(id = "first")
        val second = FakeUnveilPlugin(id = "second")
        val updatedFirst = FakeUnveilPlugin(id = "first", title = "Updated First")

        UnveilRegistry.register(first)
        UnveilRegistry.register(second)
        UnveilRegistry.register(updatedFirst)

        assertEquals(listOf(updatedFirst, second), UnveilRegistry.plugins)
    }

    @Test
    fun `plugins returns a snapshot independent of future mutations`() {
        UnveilRegistry.register(FakeUnveilPlugin(id = "snapshot_test"))
        val snapshot = UnveilRegistry.plugins

        UnveilRegistry.clearAll()

        assertEquals(1, snapshot.size)
    }

    @Test
    fun `clearAll empties the plugin list`() {
        UnveilRegistry.register(FakeUnveilPlugin(id = "to_clear"))

        UnveilRegistry.clearAll()

        assertTrue(UnveilRegistry.plugins.isEmpty())
    }
}
