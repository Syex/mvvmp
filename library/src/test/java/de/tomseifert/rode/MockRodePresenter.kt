package de.tomseifert.rode

/**
 * A simple presenter extending the [RodePresenter].
 */
class MockRodePresenter : RodePresenter<MockView>() {

    var onCreateCalled = false
    var onDestroyCalled = false
    var onViewAttachedCalled = false
    var onViewDetachedCalled = false

    fun reset() {
        view = null
        onCreateCalled = false
        onDestroyCalled = false
        onViewAttachedCalled = false
        onViewDetachedCalled = false
    }

    fun getView() = view

    fun getViewOrThrow() = viewOrThrow

    override fun onCreate() {
        onCreateCalled = true
    }

    override fun onDestroy() {
        onDestroyCalled = true
    }

    override fun onViewAttached(view: MockView) {
        onViewAttachedCalled = true
    }

    override fun onViewDetached() {
        onViewDetachedCalled = true
    }
}

interface MockView