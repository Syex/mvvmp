package de.syex.rode.test

import de.syex.rode.RodePresenter
import de.syex.rode.TestViewCommandExecutor

/**
 * Helper class to handle the lifecycle of a [RodePresenter] in a unit test.
 */
class RodeTestUtils<out T : RodePresenter<View>, View>(val presenter: T, var view: View) {

    init {
        // execute all ViewCommands on the current thread
        presenter.viewCommandExecutor = TestViewCommandExecutor()
        presenter.created()
        presenter.attachView(view)
    }

    /**
     * Mimics [android.arch.lifecycle.Lifecycle.Event.ON_START].
     *
     * Calls the presenter's [RodePresenter.attachView] with the given *view*.
     */
    fun onLifecycleStart(view: View) {
        this.view = view
        presenter.attachView(view)
    }

    /**
     * Mimics [android.arch.lifecycle.Lifecycle.Event.ON_STOP].
     *
     * Calls the presenter's [RodePresenter.detachView] to clear any reference to the *view*.
     */
    fun onLifecycleStop() {
        presenter.detachView()
    }

    /**
     * Mimics [android.arch.lifecycle.Lifecycle.Event.ON_DESTROY].
     *
     * Calls the presenter's [RodePresenter.destroy] to indicate the lifecycle of this
     * presenter is over.
     */
    fun onLifecycleDestroy() {
        presenter.destroy()
    }
}