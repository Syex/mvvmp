package de.syex.rode

import com.nhaarman.mockito_kotlin.*
import org.junit.*
import org.junit.Assert.*

/**
 *
 */
internal class ViewCommandStoreTest {

    val store = ViewCommandStore<MockView>()

    @Test
    fun `enqueues command`() {
        // whenever we enqueue a command
        val viewCommand = ViewCommand<MockView> { }
        store.enqueue(NO_TAG, viewCommand)

        val view: MockView = mock()
        val queuedCommands = store.queuedCommands(view)

        // assert that it is returned by queuedCommands
        val viewCommandWrapper = queuedCommands.poll()
        assertTrue(queuedCommands.isEmpty())
        assertEquals(NO_TAG, viewCommandWrapper.tag)
        assertEquals(viewCommand, viewCommandWrapper.command)
    }

    @Test
    fun `doesn't enqueue commands with same tag that haven't been sent`() {
        // whenever we add two view commands with same tag while no view is attached
        val viewCommand = ViewCommand<MockView> { }
        store.enqueue("tag", viewCommand)
        val anotherViewCommand = ViewCommand<MockView> { }
        store.enqueue("tag", anotherViewCommand)

        val view: MockView = mock()
        val queuedCommands = store.queuedCommands(view)

        // verify none of them is sent to the view
        assertTrue(queuedCommands.isEmpty())
    }

    @Test
    fun `enqueues command with same tag has been sent`() {
        // whenever we enqueue a command and send it to the view
        val viewCommand = ViewCommand<MockView> { }
        val wrapper = store.enqueue("tag", viewCommand)!!

        val view: MockView = mock()
        store.notifyViewCommandsSent(listOf(wrapper), view)

        // assert that there are no more commands
        assertTrue(store.queuedCommands(view).isEmpty())

        // whenever we enqueue another command with this tag
        val anotherViewCommand = ViewCommand<MockView> { }
        store.enqueue("tag", anotherViewCommand)

        var queuedCommands = store.queuedCommands(view)

        // verify this command is being returned by the store
        assertFalse(queuedCommands.isEmpty())
        // notify store we sent this command to the view
        store.notifyViewCommandsSent(listOf(queuedCommands.poll()), view)
        // the store should be empty now because commands with same tag have been removed after this call

        // whenever we get the stored commands for another view
        val anotherView: MockView = mock()
        queuedCommands = store.queuedCommands(anotherView)

        // verify there are no commands to send to the new view
        assertTrue(queuedCommands.isEmpty())
    }

    @Test
    fun `marks command as sent after being notified`() {
        val viewCommand = ViewCommand<MockView> { }
        val wrapper = store.enqueue("tag", viewCommand)!!
        val view: MockView = mock()

        // whenever we notify the store we sent a command
        store.notifyViewCommandsSent(listOf(wrapper), view)

        assertTrue(wrapper.commandSent)
        assertEquals(view.toString(), wrapper.sentToView)
    }

    @Test
    fun `replays sent commands to a new view`() {
        // whenever we enqueue a command
        val viewCommand = ViewCommand<MockView> { }
        store.enqueue(NO_TAG, viewCommand)

        val view: MockView = mock()
        val queuedCommands = store.queuedCommands(view)
        store.notifyViewCommandsSent(listOf(queuedCommands.peek()), view)

        // and then request the commands for a new view
        val anotherView: MockView = mock()
        val queuedCommandsForNewView = store.queuedCommands(anotherView)

        // assert the commands are equal
        assertEquals(queuedCommands.size, queuedCommandsForNewView.size)
        assertEquals(queuedCommands.element(), queuedCommandsForNewView.element())
    }

    @Test
    fun `commands with once tag are sent once`() {
        // whenever we enqueue a command with ONCE_TAG
        val viewCommand = ViewCommand<MockView> { }
        store.enqueue(ONCE_TAG, viewCommand)

        val view: MockView = mock()
        val queuedCommands = store.queuedCommands(view)
        store.notifyViewCommandsSent(listOf(queuedCommands.peek()), view)

        // and then request the commands for a new view
        val anotherView: MockView = mock()
        val queuedCommandsForNewView = store.queuedCommands(anotherView)

        // assert the commands for the new view are empty
        assertTrue(queuedCommandsForNewView.isEmpty())
    }
}