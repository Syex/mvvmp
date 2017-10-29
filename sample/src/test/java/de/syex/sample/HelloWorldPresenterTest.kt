package de.syex.sample

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HelloWorldPresenterTest {

    val presenter = HelloWorldPresenter()
    val view: HelloWordView = mock()
    val testUtils = presenter.test(view)

    @Test
    fun `sets hello world text on view attach`() {
        verify(view).setHelloWorldText(any())
    }

    @Test
    fun `sets hello world text on the same view only once`() {
        // whenever the same view is detached and attached to the presenter again
        testUtils.onLifecycleStop()
        testUtils.onLifecycleStart(view)

        // verify there was no additional invocation
        verify(view, times(1)).setHelloWorldText(any())
    }

    @Test
    fun `sets hello world text on a new view`() {
        // whenever a new view attaches to the presenter
        val newView: HelloWordView = mock()

        testUtils.onLifecycleStop()
        testUtils.onLifecycleStart(newView)

        // verify the stored ViewCommand is executed again, automatically
        verify(newView).setHelloWorldText(any())
    }
}