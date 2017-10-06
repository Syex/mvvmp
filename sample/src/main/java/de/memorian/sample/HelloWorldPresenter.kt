package de.memorian.sample

import android.util.Log
import de.memorian.mvvmp.RodePresenter

/**
 * An implementation of a *presenter* from the MVP architecture, extending Rode's base presenter.
 */
class HelloWorldPresenter(val text: String) : RodePresenter<HelloWordView>() {

    override fun onCreate() {
        Log.i("HWPresenter", "onCreate called")
    }

    override fun onDestroy() {
        Log.i("HWPresenter", "onDestroy called")
    }

    override fun onViewAttached(view: HelloWordView) {
        view.showHelloWord(text)
        Log.i("HWPresenter", "onViewAttached called with view $view")
    }

    override fun onViewDetached() {
        Log.i("HWPresenter", "onViewDetached called")
    }
}