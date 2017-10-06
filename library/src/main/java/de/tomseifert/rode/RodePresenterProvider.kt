package de.tomseifert.rode

import android.arch.lifecycle.LifecycleOwner

/**
 * Interface that needs to be implemented by a [LifecycleOwner] (typically an *Activity* or a *Fragment*)
 * that needs a *createPresenter*.
 *
 * @param P The type of a *createPresenter* extending [RodePresenter].
 * @param V The type of the *view* that *P* expects.
 */
interface RodePresenterProvider<out P : RodePresenter<V>, V> : LifecycleOwner {

    /**
     * @return An instance of a *presenter* of type *P*.
     */
    fun createPresenter(): P
}