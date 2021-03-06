package de.syex.rode

import de.syex.rode.test.RodeTestUtils


/**
 * Base class for *presenters* of the MVP architecture.
 *
 * It keeps a reference to the *view*, which will be automatically attached and detached to prevent memory leaks.
 *
 * @param View The type of the *view* this *presenter* expects.
 */
abstract class RodePresenter<View> {

    /**
     * Executor that executes any [ViewCommands][ViewCommand]. By default all will be executed
     * by a [UiThreadViewCommandExecutor].
     */
    internal var viewCommandExecutor: ViewCommandExecutor = UiThreadViewCommandExecutor()

    /**
     * Stores [ViewCommands][ViewCommand] that were sent to the *view* or will be sent as soon as a *view* attaches.
     *
     * When a different *view* attaches all *view commands* that were sent to the previous *view* via [sendToView]
     * will be resent to the new *view*.
     *
     * @see sendToView
     * @see sendToViewOnce
     */
    private val viewCommandStore = ViewCommandStore<View>()
    /**
     * The *view* this *presenter* instructs. Might be *null* if currently no *view* is attached.
     */
    protected var view: View? = null

    /**
     * Called by a [RodeLifecycleObserver] when it creates this instance.
     */
    internal fun created() {
        onCreate()
    }

    /**
     * Called by a [RodeLifecycleObserver] when [RodeViewModel.onCleared] gets called.
     */
    internal fun destroy() {
        detachView()
        viewCommandStore.dispose()
        onDestroy()
    }

    /**
     * Attaches *view* to this instance.
     */
    internal fun attachView(view: View) {
        detachView()
        this.view = view
        sendQueuedViewCommands(view)
        onViewAttached(view)
    }

    /**
     * Removes the reference to [view] from this instance.
     */
    internal fun detachView() {
        val viewAttached = view != null
        view = null

        // Only call onViewDetached() if there was an attached view before this call
        if (viewAttached) {
            onViewDetached()
        }
    }

    /**
     * Called when this *presenter* got a reference to a *view*.
     *
     * @param view The *view* that has been attached.
     */
    protected open fun onViewAttached(view: View) {

    }

    /**
     * Called when the *view* got removed from this *presenter*.
     */
    protected open fun onViewDetached() {

    }

    /**
     * Called right after this instance has been created.
     */
    protected open fun onCreate() {

    }

    /**
     * Called when this instance is no longer needed. Stop any ongoing tasks that could keep a reference to this
     * object that might prevent the garbage collector from removing this object.
     */
    protected open fun onDestroy() {

    }

    /**
     * Convenience function to get [view] as a non *null* object.
     *
     * Only use this if you're sure the *view* is not *null*, otherwise it will throw a
     * [NullPointerException].
     */
    protected fun unwrappedView() = view!!

    /**
     * Sends *viewCommand* to the *view*, once, and doesn't remember it.
     *
     * If a *view* is currently attached it will be executed immediately
     * on the UI thread, otherwise it will be executed as soon as a *view* gets attached.
     *
     * @see sendToView
     */
    protected fun sendToViewOnce(viewCommand: ViewCommand<View>) {
        if (view != null) {
            viewCommandExecutor.execute(viewCommand, view!!)
        } else {
            viewCommandStore.enqueue(ONCE_TAG, viewCommand)
        }
    }

    /**
     * Sends *viewCommand* to the *view* and remembers the command for the lifecycle of this *presenter*.
     *
     * If a *view* is currently attached it will be executed immediately
     * on the UI thread, otherwise it will be executed as soon as a *view* gets attached.
     *
     * If the *view* detaches and a new *view* attaches again, the *viewCommand* will also be executed again.
     * By providing the same [tag][ViewCommand.tag] you can group [ViewCommandStore][ViewCommand] to be exclusive.
     *
     * For example, imagine you have two methods *setError()* and *clearError()*. By setting the same
     * [tag][ViewCommand.tag] to each *ViewCommand*, the *clearError()* *ViewCommand* will erase the first sent
     * *setError()* *ViewCommand* in the history. None of these both commands will be sent to the new view as a
     * result.
     *
     * @see sendToViewOnce
     */
    protected fun sendToView(viewCommand: ViewCommand<View>, tag: String = NO_TAG) {
        if (tag == ONCE_TAG) {
            sendToViewOnce(viewCommand)
            return
        }

        val viewCommandWrapper = viewCommandStore.enqueue(tag, viewCommand)

        if (view != null) {
            viewCommandExecutor.execute(viewCommand, view!!)
            viewCommandWrapper?.let {
                viewCommandStore.notifyViewCommandsSent(listOf(it), view!!)
            }
        }
    }

    private fun sendQueuedViewCommands(view: View) {
        val queuedCommands = viewCommandStore.queuedCommands(view)
        val sentCommands = mutableListOf<ViewCommandWrapper<View>>()
        while (queuedCommands.isNotEmpty()) {
            val wrapper = queuedCommands.poll()
            sendToViewOnce(wrapper.command)
            sentCommands.add(wrapper)
        }
        viewCommandStore.notifyViewCommandsSent(sentCommands, view)
    }

    /**
     * Prepares this *presenter* for units tests.
     *
     * All commands sent to the *view* via [sendToView] or [sendToViewOnce] will be executed with a
     * [TestViewCommandExecutor].
     */
    fun testO(): RodePresenter<View> {
        viewCommandExecutor = TestViewCommandExecutor()
        return this
    }

    /**
     * Prepares this *presenter* for units tests.
     *
     * All commands sent to the *view* via [sendToView] or [sendToViewOnce] will be executed with a
     * [TestViewCommandExecutor].
     */
    fun test(view: View) = RodeTestUtils(this, view)

}