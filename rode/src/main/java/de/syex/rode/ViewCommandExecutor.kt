package de.syex.rode

import android.os.Handler
import android.os.Looper

/**
 * Interface for classes that execute [ViewCommands][ViewCommand].
 */
interface ViewCommandExecutor {

    /**
     * Sends the given *viewCommand* to *view* with *view* as its parameter.
     */
    fun <View> execute(viewCommand: ViewCommand<View>, view: View)
}

/**
 * [ViewCommandExecutor] that executes [ViewCommands][ViewCommand] on Android's main thread.
 */
internal class UiThreadViewCommandExecutor : ViewCommandExecutor {

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun <View> execute(viewCommand: ViewCommand<View>, view: View) {
        handler.post { viewCommand.execute(view) }
    }
}

/**
 * [ViewCommandExecutor] that executes [ViewCommands][ViewCommand] on the current thread.
 */
internal class TestViewCommandExecutor : ViewCommandExecutor {

    override fun <View> execute(viewCommand: ViewCommand<View>, view: View) {
        viewCommand.execute(view)
    }
}