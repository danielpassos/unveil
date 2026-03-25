package me.passos.libs.unveil.core.drawer

import kotlinx.coroutines.test.runTest
import me.passos.libs.unveil.FakeUnveilPlugin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DrawerControllerTest {

    // --- Initial state ---

    @Test
    fun `pageStack starts with a single PluginList entry`() {
        val controller = DrawerController()

        assertEquals(1, controller.pageStack.size)
        assertIs<DrawerPage.PluginList>(controller.pageStack[0])
    }

    @Test
    fun `currentPage is PluginList initially`() {
        val controller = DrawerController()

        assertIs<DrawerPage.PluginList>(controller.currentPage)
    }

    @Test
    fun `isOpen is false initially`() {
        val controller = DrawerController()

        assertFalse(controller.isOpen)
    }

    // --- snapTo / isOpen ---

    @Test
    fun `snapTo sets the fraction to the given value`() = runTest {
        val controller = DrawerController()

        controller.snapTo(0.5f)

        assertEquals(0.5f, controller.translationXFraction.value)
    }

    @Test
    fun `snapTo coerces negative values to 0`() = runTest {
        val controller = DrawerController()

        controller.snapTo(-1f)

        assertEquals(0f, controller.translationXFraction.value)
    }

    @Test
    fun `snapTo coerces values above 1 to 1`() = runTest {
        val controller = DrawerController()

        controller.snapTo(2f)

        assertEquals(1f, controller.translationXFraction.value)
    }

    @Test
    fun `isOpen is true when fraction is above 0`() = runTest {
        val controller = DrawerController()

        controller.snapTo(0.1f)

        assertTrue(controller.isOpen)
    }

    @Test
    fun `isOpen is false when fraction is exactly 0`() = runTest {
        val controller = DrawerController()

        controller.snapTo(0f)

        assertFalse(controller.isOpen)
    }

    // --- navigateTo ---

    @Test
    fun `navigateTo from root pushes a PluginPage`() {
        val controller = DrawerController()

        controller.navigateTo(FakeUnveilPlugin())

        assertEquals(2, controller.pageStack.size)
        assertIs<DrawerPage.PluginPage>(controller.currentPage)
    }

    @Test
    fun `navigateTo sets currentPage to the given plugin`() {
        val controller = DrawerController()
        val plugin = FakeUnveilPlugin(id = "target")

        controller.navigateTo(plugin)

        val page = assertIs<DrawerPage.PluginPage>(controller.currentPage)
        assertEquals(plugin, page.plugin)
    }

    @Test
    fun `navigateTo from a PluginPage replaces it keeping stack size at 2`() {
        val controller = DrawerController()

        controller.navigateTo(FakeUnveilPlugin(id = "first"))
        controller.navigateTo(FakeUnveilPlugin(id = "second"))

        assertEquals(2, controller.pageStack.size)
    }

    @Test
    fun `navigateTo from a PluginPage shows the new plugin`() {
        val controller = DrawerController()
        val second = FakeUnveilPlugin(id = "second")

        controller.navigateTo(FakeUnveilPlugin(id = "first"))
        controller.navigateTo(second)

        val page = assertIs<DrawerPage.PluginPage>(controller.currentPage)
        assertEquals(second, page.plugin)
    }

    @Test
    fun `navigateTo clears any SubPage before switching plugin`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin(id = "first"))
        scope.pushPage("Detail") {}
        controller.navigateTo(FakeUnveilPlugin(id = "second"))

        assertEquals(2, controller.pageStack.size)
    }

    // --- navigateBack ---

    @Test
    fun `navigateBack from PluginPage returns to PluginList`() {
        val controller = DrawerController()

        controller.navigateTo(FakeUnveilPlugin())
        controller.navigateBack()

        assertIs<DrawerPage.PluginList>(controller.currentPage)
    }

    @Test
    fun `navigateBack from PluginPage returns true`() {
        val controller = DrawerController()

        controller.navigateTo(FakeUnveilPlugin())

        assertTrue(controller.navigateBack())
    }

    @Test
    fun `navigateBack from PluginList returns false`() {
        val controller = DrawerController()

        assertFalse(controller.navigateBack())
    }

    @Test
    fun `navigateBack from PluginList does not mutate the stack`() {
        val controller = DrawerController()

        controller.navigateBack()

        assertEquals(1, controller.pageStack.size)
    }

    @Test
    fun `navigateBack from SubPage returns to PluginPage`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("Detail") {}
        controller.navigateBack()

        assertIs<DrawerPage.PluginPage>(controller.currentPage)
    }

    // --- resetToPluginList ---

    @Test
    fun `resetToPluginList at root is a no-op`() {
        val controller = DrawerController()

        controller.resetToPluginList()

        assertEquals(1, controller.pageStack.size)
        assertIs<DrawerPage.PluginList>(controller.currentPage)
    }

    @Test
    fun `resetToPluginList from PluginPage returns to root`() {
        val controller = DrawerController()

        controller.navigateTo(FakeUnveilPlugin())
        controller.resetToPluginList()

        assertEquals(1, controller.pageStack.size)
        assertIs<DrawerPage.PluginList>(controller.currentPage)
    }

    @Test
    fun `resetToPluginList from SubPage returns to root`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("Detail") {}
        controller.resetToPluginList()

        assertEquals(1, controller.pageStack.size)
        assertIs<DrawerPage.PluginList>(controller.currentPage)
    }

    // --- createPanelScope / pushPage / popPage ---

    @Test
    fun `pushPage adds a SubPage to the stack`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("Detail") {}

        assertEquals(3, controller.pageStack.size)
        assertIs<DrawerPage.SubPage>(controller.currentPage)
    }

    @Test
    fun `pushPage sets the title on the SubPage`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("Request Detail") {}

        val page = assertIs<DrawerPage.SubPage>(controller.currentPage)
        assertEquals("Request Detail", page.title)
    }

    @Test
    fun `pushPage replaces an existing SubPage instead of stacking`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("First") {}
        scope.pushPage("Second") {}

        assertEquals(3, controller.pageStack.size)
        val page = assertIs<DrawerPage.SubPage>(controller.currentPage)
        assertEquals("Second", page.title)
    }

    @Test
    fun `popPage removes the SubPage`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.pushPage("Detail") {}
        scope.popPage()

        assertEquals(2, controller.pageStack.size)
        assertIs<DrawerPage.PluginPage>(controller.currentPage)
    }

    @Test
    fun `popPage is a no-op when there is no SubPage`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        controller.navigateTo(FakeUnveilPlugin())
        scope.popPage()

        assertEquals(2, controller.pageStack.size)
    }

    @Test
    fun `popPage at PluginList is a no-op`() {
        val controller = DrawerController()
        val scope = controller.createPanelScope()

        scope.popPage()

        assertEquals(1, controller.pageStack.size)
    }
}
