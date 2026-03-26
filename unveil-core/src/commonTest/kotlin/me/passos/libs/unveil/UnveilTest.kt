package me.passos.libs.unveil

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UnveilTest {

    @BeforeTest
    fun setUp() {
        Unveil.disable()
        Unveil.registry.clearAll()
    }

    @AfterTest
    fun tearDown() {
        Unveil.disable()
        Unveil.registry.clearAll()
    }

    @Test
    fun `isEnabled is false before enable is called`() {
        assertFalse(Unveil.isEnabled)
    }

    @Test
    fun `enable sets isEnabled to true`() {
        Unveil.enable()

        assertTrue(Unveil.isEnabled)
    }

    @Test
    fun `disable sets isEnabled to false`() {
        Unveil.enable()
        Unveil.disable()

        assertFalse(Unveil.isEnabled)
    }

    @Test
    fun `configure registers the plugin in the registry`() {
        val plugin = FakeUnveilPlugin(id = "test_plugin")

        Unveil.configure { register(plugin) }

        assertEquals(1, Unveil.registry.plugins.size)
        assertSame(plugin, Unveil.registry.plugins[0])
    }

    @Test
    fun `calling configure multiple times registers plugins from each call`() {
        Unveil.configure { register(FakeUnveilPlugin(id = "first")) }
        Unveil.configure { register(FakeUnveilPlugin(id = "second")) }

        assertEquals(2, Unveil.registry.plugins.size)
    }

    @Test
    fun `configure with duplicate id replaces the existing plugin`() {
        val original = FakeUnveilPlugin(id = "dup")
        val replacement = FakeUnveilPlugin(id = "dup", title = "Replacement")

        Unveil.configure { register(original) }
        Unveil.configure { register(replacement) }

        assertEquals(1, Unveil.registry.plugins.size)
        assertSame(replacement, Unveil.registry.plugins[0])
    }
}
