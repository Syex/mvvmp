package de.syex.sample

import android.util.Log
import de.syex.rode.RodePresenter
import de.syex.rode.ViewCommand

/**
 * An implementation of a *presenter* from the MVP architecture, extending Rode's base presenter.
 */
class HelloWorldPresenter() : RodePresenter<HelloWordView>() {

    private var firstErrorSet = false
    private var secondErrorSet = false

    override fun onCreate() {
        Log.i("HWPresenter", "onCreate called")
    }

    override fun onDestroy() {
        Log.i("HWPresenter", "onDestroy called")
    }

    override fun onViewAttached(view: HelloWordView) {
        sendToViewOnce(ViewCommand { it.setHelloWorldText("Hello World from the presenter") })
        Log.i("HWPresenter", "onViewAttached called with view $view")
    }

    override fun onViewDetached() {
        Log.i("HWPresenter", "onViewDetached called")
    }

    fun onClickToggleFirst() {
        if (firstErrorSet) {
            sendToView(tag = "first", viewCommand = ViewCommand { it.clearFirstError() })
        } else {
            sendToView(tag = "first", viewCommand = ViewCommand { it.showFirstError() })
        }
        firstErrorSet = !firstErrorSet
    }

    fun onClickToggleSecond() {
        if (secondErrorSet) {
            sendToView(tag = "second", viewCommand = ViewCommand { it.clearSecondError() })
        } else {
            sendToView(tag = "second", viewCommand = ViewCommand { it.showSecondError() })
        }
        secondErrorSet = !secondErrorSet
    }
}