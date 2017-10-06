package de.tomseifert.rode

import android.arch.lifecycle.ViewModel

/**
 * The *RodeViewModel* provides a stateful [RodePresenter] that will be able to be garbage collected only
 * once [ViewModel.onCleared] is called.
 */
@Suppress("UNCHECKED_CAST")
class RodeViewModel : ViewModel() {

    /**
     * The *observer* that observes the [Lifecycle][android.arch.lifecycle.Lifecycle] of the component this
     * [ViewModel] belongs to.
     */
    internal var lifecycleObserver: RodeLifecycleObserver<*, *>? = null

    /**
     * Provides a *createPresenter* of type *P*.
     *
     * If this method is called the first time on this [ViewModel], the provided *presenterProvider* will be
     * asked to provide an instance, otherwise *presenterProvider* will not be called in any way and the instance
     * from the previous call with be returned instead.
     *
     * @param presenterProvider Used to provide a *createPresenter* instance if this [ViewModel] doesn't already have one.
     * @param P The type of a *createPresenter* extending [RodePresenter].
     * @param V The type of the *view* that *P* expects.
     * @return An instance of *P*.
     */
    fun <P : RodePresenter<V>, V> providePresenter(presenterProvider: RodePresenterProvider<P, V>): P {
        if (lifecycleObserver == null) {
            lifecycleObserver = RodeLifecycleObserver(presenterProvider)
        }
        presenterProvider.lifecycle.addObserver(lifecycleObserver)

        return (lifecycleObserver as RodeLifecycleObserver<P, V>).presenter
    }

    override fun onCleared() {
        lifecycleObserver?.destroy()
        lifecycleObserver = null
    }
}

