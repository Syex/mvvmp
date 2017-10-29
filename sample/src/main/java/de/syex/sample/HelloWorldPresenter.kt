package de.syex.sample

import de.syex.rode.RodePresenter
import de.syex.rode.ViewCommand

/**
 * An implementation of a *presenter* from the MVP architecture, extending Rode's base presenter.
 */
class HelloWorldPresenter : RodePresenter<HelloWordView>() {

    private var firstErrorSet = false
    private var secondErrorSet = false

    override fun onCreate() {
        sendToView(ViewCommand { it.setHelloWorldText("Hello World from the presenter") })
    }

    override fun onDestroy() {
    }

    override fun onViewAttached(view: HelloWordView) {
    }

    override fun onViewDetached() {
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