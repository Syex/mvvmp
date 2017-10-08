package de.syex.rode

import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 *
 */
@RunWith(JUnit4::class)
internal class RodePresenterTest {

    val presenter = MockRodePresenter()
    val view = mock<MockView>()

    @Before
    fun setUp() {
        // Reset MockPresenter before every test
        presenter.reset()
    }

    @Test
    fun `create calls onCreate`() {
        presenter.created()

        assertTrue(presenter.onCreateCalled)
    }

    @Test
    fun `destroy calls onDestroy`() {
        presenter.destroy()

        assertTrue(presenter.onDestroyCalled)
    }

    @Test
    fun `attachView calls onViewAttached`() {
        presenter.attachView(view)

        assertTrue(presenter.onViewAttachedCalled)
    }

    @Test
    fun `detachView calls onViewDetached`() {
        // verify a call to detachView while no view attached doesn't delegate the call
        presenter.detachView()
        assertFalse(presenter.onViewDetachedCalled)

        `attachView calls onViewAttached`()
        presenter.detachView()
        assertTrue(presenter.onViewDetachedCalled)
    }

    @Test(expected = NullPointerException::class)
    fun `viewOrThrow throws NPE`() {
        presenter.getViewOrThrow()
    }

    @Test
    fun `view being attached`() {
        // assert view is null at the beginning
        assertNull(presenter.getView())

        // attach a view
        presenter.attachView(view)

        // assert the view is set now
        assertNotNull(presenter.getView())
        assertNotNull(presenter.getViewOrThrow())

        // and appropriate methods have been called
        assertTrue(presenter.onViewAttachedCalled)
    }

    @Test
    fun `detach views removes view reference`() {
        // given a presenter with an attached view
        `view being attached`()

        presenter.detachView()

        // assert view reference is cleared
        assertNull(presenter.getView())
        assertTrue(presenter.onViewDetachedCalled)
    }

    @Test
    fun `attaching a new view detached the old one`() {
        // attach a view
        `view being attached`()
        // reset mock presenter
        presenter.onViewAttachedCalled = false

        val newView = mock<MockView>()
        presenter.attachView(newView)

        assertTrue(presenter.onViewDetachedCalled)
        assertTrue(presenter.onViewAttachedCalled)
    }
}