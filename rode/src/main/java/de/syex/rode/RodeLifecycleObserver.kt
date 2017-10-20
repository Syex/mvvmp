package de.syex.rode

import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent


/**
 * A [LifecycleObserver] that keeps a reference to a [RodePresenter] instance.
 *
 * * Upon the lifecycle event [ON_START] the [LifecycleOwner] calling this [LifecycleObserver] will be attached
 * to the *presenter* instance as its *view*.
 * * Upon the lifecycle event [ON_STOP] the [LifecycleOwner] calling this [LifecycleObserver] will be detached
 * from the *presenter* instance.
 * * Upon the lifecycle event [ON_DESTROY] this [LifecycleObserver] will unregister as an observer from the
 * calling [LifecycleOwner] to prevent any leaks.
 *
 * @param P The type of a *presenter* extending [RodePresenter].
 * @param V The type of the *view* that *P* expects.
 * @property presenterProvider Will be called to create an instance of *P* upon creating this object.
 */
@Suppress("UNCHECKED_CAST")
internal class RodeLifecycleObserver<out P : RodePresenter<V>, V>(
        presenterProvider: RodePresenterProvider<P, V>
) : LifecycleObserver {

    /**
     * A reference to a stateful *presenter* that only gets created once, when this [LifecycleObserver] is created.
     *
     * Upon creating the *presenter*, its [created()][RodePresenter.created] method gets called.
     */
    val presenter: P = presenterProvider.createPresenter().apply { created() }

    /**
     * Called by the [LifecycleOwner] this *observer* is registered to when it moved to the *started* state.
     *
     * The [LifecycleOwner] is expected to be an instance of [V] and will be attached to [presenter].
     */
    @OnLifecycleEvent(ON_START)
    fun onStart(lifecycleOwner: LifecycleOwner) {
        presenter.attachView(lifecycleOwner as V)
    }

    /**
     * Called by the [LifecycleOwner] this *observer* is registered to when it moved to the *stopped* state.
     *
     * Calls [RodePresenter.detachView()] to clear any reference to [V].
     */
    @OnLifecycleEvent(ON_STOP)
    fun onStop(lifecycleOwner: LifecycleOwner) {
        presenter.detachView()
    }

    /**
     * Called by the [LifecycleOwner] this *observer* is registered to when it moved to the *destroyed* state.
     *
     * Unregisters this [LifecycleObserver] from the the lifecycle of [lifecycleOwner].
     */
    @OnLifecycleEvent(ON_DESTROY)
    fun onDestroy(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    /**
     * Instruct this object to clear any references and clean up.
     *
     * _Caution:_ Does not unregister from any [Lifecycle][android.arch.lifecycle.Lifecycle]. This is normally
     * done before in [onDestroy].
     */
    fun destroy() {
        presenter.destroy()
    }
}