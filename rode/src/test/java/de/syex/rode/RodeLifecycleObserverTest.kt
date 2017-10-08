package de.syex.rode

import android.arch.lifecycle.Lifecycle
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 *
 */
@RunWith(JUnit4::class)
internal class RodeLifecycleObserverTest {

    val presenter = MockRodePresenter()
    val lifecycle = mock<Lifecycle>()
    val testView = mock<TestView> {
        on { createPresenter() } doReturn presenter
        on { lifecycle } doReturn lifecycle
    }
    val lifecycleObserver = RodeLifecycleObserver<MockRodePresenter, MockView>(testView)

    @Test
    fun `calls presenter onCreate`() {
        assertEquals(presenter, lifecycleObserver.presenter)
        assertTrue(presenter.onCreateCalled)
    }

    @Test
    fun `lifecycle event ON_START attaches view`() {
        lifecycleObserver.onStart(testView)

        assertEquals(presenter.getView(), testView)
    }

    @Test
    fun `lifecycle event ON_STOP detaches view`() {
        lifecycleObserver.onStop(testView)

        assertNull(presenter.getView())
    }

    @Test
    fun `lifecycle event ON_DESTROY stops observing`() {
        lifecycleObserver.onDestroy(testView)

        verify(lifecycle).removeObserver(lifecycleObserver)
    }
}
