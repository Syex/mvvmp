package de.syex.rode

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue

internal const val NO_TAG = "default_tag"
internal const val ONCE_TAG = "once_tag"

/**
 * Wrapper class that stores [ViewCommandStore][ViewCommand] to send to a *view*, as also a history of all commands
 * sent to the view in the lifecycle of this object.
 */
internal class ViewCommandStore<View> {

    /**
     * The history of all commands sent to the *view*
     */
    private val commandHistory = LinkedBlockingQueue<ViewCommandWrapper<View>>()

    /**
     * Returns the commands that are in queue for the given *view*.
     *
     * The returned queue will contain commands that
     *  * Have not been sent to a view.
     *  * Have been sent to a different view but the given *view*.
     */
    fun queuedCommands(view: View): LinkedBlockingQueue<ViewCommandWrapper<View>> {
        val queuedCommands = mutableListOf<ViewCommandWrapper<View>>()
        for (wrapper in commandHistory) {
            if (!wrapper.commandSent) {
                queuedCommands.add(wrapper)
            }

            if (wrapper.commandSent && view.toString() != wrapper.sentToView) {
                queuedCommands.add(wrapper)
            }
        }
        return LinkedBlockingQueue(queuedCommands)
    }

    /**
     * Notifies this store that the given list of [ViewCommandWrapper] has been sent to the view.
     *
     * This will set [ViewCommandWrapper.commandSent] to *true*. Further:
     *  * All of the sent commands with tag [ONCE_TAG] will be removed from the internal history.
     *  * Commands with the same tag, that both have been sent to the view, now, will be removed from the internal
     *  history.
     */
    fun notifyViewCommandsSent(sentViewCommands: List<ViewCommandWrapper<View>>, toView: View) {
        for (wrapper in sentViewCommands) {
            wrapper.commandSent = true
            wrapper.sentToView = toView.toString()

            // remove all commands that should only be executed once, so they will not be queued again
            if (wrapper.tag == ONCE_TAG) {
                commandHistory.remove(wrapper)
                continue
            }

            if (wrapper.tag == NO_TAG) {
                continue
            }

            // if we sent two commands with the same tag to the view, we don't need to keep them in history
            val commandWrapperWithSameTag = commandHistory.find { it != wrapper && it.tag == wrapper.tag }
            if (wrapper.commandSent && commandWrapperWithSameTag?.commandSent == true) {
                commandHistory.remove(wrapper)
                commandHistory.remove(commandWrapperWithSameTag)
            }
        }
    }

    /**
     * Adds *viewCommand* to a FIFO queue.
     *
     * If a [ViewCommand] with the same [tag][ViewCommand.tag] is already
     * in the queue and it wasn't sent to the view, yet, the stored *viewCommand* gets removed instead
     * and nothing will be added or executed.
     *
     * If the command was already sent, the new command will be added to history and both will be removed, as
     * soon as the new command was sent.
     *
     * @return The created [ViewCommandWrapper] if the *viewCommand* was enqueued, else *null*.
     */
    fun enqueue(tag: String, viewCommand: ViewCommand<View>): ViewCommandWrapper<View>? {
        // check if there's already a ViewCommand with this tag in history
        val commandWrapperWithSameTag = if (tag == NO_TAG) null else commandHistory.find {
            it.tag == tag
        }

        // if not or this command was already sent
        if (commandWrapperWithSameTag == null || commandWrapperWithSameTag.commandSent) {
            val viewCommandWrapper = ViewCommandWrapper(tag, viewCommand)
            commandHistory.add(viewCommandWrapper)
            return viewCommandWrapper
        }

        // else we don't need to enqueue the ViewCommand that has the same tag and can remove the stored
        // commandWrapperWithSameTag
        else {
            commandHistory.remove(commandWrapperWithSameTag)
        }

        return null
    }

    /**
     * Disposes all resources of this store.
     */
    fun dispose() {
        commandHistory.clear()
    }
}

/**
 * Wraps a [ViewCommand] together with a *tag*.
 */
class ViewCommandWrapper<View>(val tag: String = ONCE_TAG, val command: ViewCommand<View>) {

    /**
     * Flag whether the *command* has already been sent to a *view*.
     */
    var commandSent = false
    /**
     * Identifier of the *view* the *command* has been sent to.
     */
    var sentToView: String? = null
}

interface ViewCommandExecutor {

    fun <View> execute(viewCommand: ViewCommand<View>, view: View)
}

internal class UiThreadViewCommandExecutor : ViewCommandExecutor {

    private val handler = Handler(Looper.getMainLooper())

    override fun <View> execute(viewCommand: ViewCommand<View>, view: View) {
        handler.post { viewCommand.execute(view) }
    }
}