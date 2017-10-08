package de.tomseifert.rode

import android.arch.lifecycle.Lifecycle
import com.nhaarman.mockito_kotlin.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*
import org.junit.runners.*

/**
 *
 */
@RunWith(JUnit4::class)
internal class RodeViewModelTest {

    val viewModel = RodeViewModel()
    val presenter = MockRodePresenter()
    val lifecycle = mock<Lifecycle>()
    val presenterProvider = mock<RodePresenterProvider<MockRodePresenter, MockView>> {
        on { createPresenter() } doReturn presenter
        on { lifecycle } doReturn lifecycle
    }

    @Test
    fun `creates a new presenter instance`() {
        // verify the by the viewModel provided presenter is our expected one
        assertEquals(presenter, viewModel.providePresenter(presenterProvider))

        // verfiy the presenter provider was called
        verify(presenterProvider).createPresenter()
        // verify the viewModel added a lifecycle observer
        verify(lifecycle).addObserver(any())
    }

    @Test
    fun `reuses an existing presenter`() {
        `creates a new presenter instance`()

        // verify multiple calls to providePresenter return the same instance
        assertEquals(presenter, viewModel.providePresenter(presenterProvider))
        // verfify the presenter provider was not called again
        verify(presenterProvider, times(1)).createPresenter()
        // verify a lifecycle observer is only registered once
        verify(lifecycle, times(1)).addObserver(any())
    }

    @Test
    fun `onCleared destroys presenter`() {
        // make sure the viewModel has a presenter
        viewModel.providePresenter(presenterProvider)

        // make ViewModel.onCleared accessible
        val onClearedMethod = viewModel.javaClass.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        // call onCleared
        onClearedMethod.invoke(viewModel)

        // assert presenters destroy() method has been called
        assertTrue(presenter.onDestroyCalled)
    }
}