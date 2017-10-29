package de.syex.rode

/**
 * A simple presenter extending the [RodePresenter].
 */
internal class MockRodePresenter : RodePresenter<MockView>() {

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

    fun sendToViewOnceMock(viewCommand: ViewCommand<MockView>) {
        super.sendToViewOnce(viewCommand)
    }

    fun sendToViewMock(tag: String = NO_TAG, viewCommand: ViewCommand<MockView>) {
        super.sendToView(viewCommand, tag)
    }
}

internal interface MockView

/**
 * Simulates an Activity or Fragment that implements both, RodePresenterProvider and MockView
 */
internal abstract class TestView : RodePresenterProvider<MockRodePresenter, MockView>, MockView