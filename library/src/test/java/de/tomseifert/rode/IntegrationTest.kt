package de.tomseifert.rode

import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleRegistry
import com.nhaarman.mockito_kotlin.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*
import org.junit.runners.*

/**
 *
 */
@RunWith(JUnit4::class)
internal class IntegrationTest {

    val presenter = MockRodePresenter()
    val testView = mock<TestView> {
        on { createPresenter() } doReturn presenter
    }
    val lifecycleRegistry = LifecycleRegistry(testView)
    val viewModel = RodeViewModel()

    @Before
    fun setUp() {
        whenever(testView.lifecycle).thenReturn(lifecycleRegistry)
        // Simulate a presenter has been requested from an Activity/Fragment
        viewModel.providePresenter(testView)
    }

    @Test
    fun `ON_START attaches view`() {
        // given a created presenter
        assertNull(presenter.getView())
        // LifecycleOwner fired ON_START
        lifecycleRegistry.handleLifecycleEvent(ON_START)
        // verify presenter has a view attached now
        assertEquals(testView, presenter.getView())
    }

    @Test
    fun `ON_STOP detaches view`() {
        // let the framework attach a view
        lifecycleRegistry.handleLifecycleEvent(ON_START)

        lifecycleRegistry.handleLifecycleEvent(ON_STOP)
        // verify presenter has no view anymore
        assertNull(presenter.getView())
    }

    @Test
    fun `ON_DESTROY unregisters lifecycle observer`() {
        lifecycleRegistry.handleLifecycleEvent(ON_START)
        assertEquals(1, lifecycleRegistry.observerCount)
        lifecycleRegistry.handleLifecycleEvent(ON_DESTROY)
        assertEquals(0, lifecycleRegistry.observerCount)
    }

    @Test
    fun `presenter survives configuration changes`() {
        // a view has been created and attached to the presenter
        `ON_START attaches view`()
        // the view is being destroyed
        `ON_STOP detaches view`()
        `ON_DESTROY unregisters lifecycle observer`()
        // verify presenter still exists
        assertNotNull(viewModel.lifecycleObserver?.presenter)

        // a new view is created
        val newView = mock<TestView>()
        // this view would return another instance of a RodePresenter
        whenever(newView.createPresenter()).thenReturn(MockRodePresenter())
        val newViewLifecycleRegistry = LifecycleRegistry(newView)
        whenever(newView.lifecycle).thenReturn(newViewLifecycleRegistry)

        // the new view requests a presenter
        val newViewPresenter = viewModel.providePresenter(newView)
        // verify it got the already existing presenter
        assertEquals(presenter, newViewPresenter)
        verify(newView, never()).createPresenter()

        // verify we observe the lifecycle of the new view's lifecycle
        assertEquals(0, lifecycleRegistry.observerCount)
        assertEquals(1, newViewLifecycleRegistry.observerCount)

        // newView fires ON_START
        newViewLifecycleRegistry.handleLifecycleEvent(ON_START)
        // verify our existing presenter has a reference to the new view
        assertEquals(newView, presenter.getView())
    }
}