package de.syex.rode

/**
 * Base class for *presenters* of the MVP architecture.
 *
 * It keeps a reference to the *view*, which will be automatically attached and detached to prevent memory leaks.
 *
 * @param View The type of the *view* this *presenter* expects.
 */
abstract class RodePresenter<View> {

    /**
     * The *view* this *presenter* instructs. Might be *null* if currently no *view* is attached.
     */
    protected var view: View? = null
    /**
     * The [view] if it is not *null*. Otherwise throws a [NullPointerException]. Shortcut to avoid *null* checks.
     */
    protected val viewOrThrow: View
        get() = view ?: throw NullPointerException()

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
        onDestroy()
    }

    /**
     * Attaches *view* to this instance.
     */
    internal fun attachView(view: View) {
        detachView()
        this.view = view
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
}