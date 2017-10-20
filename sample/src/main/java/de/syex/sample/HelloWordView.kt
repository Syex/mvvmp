package de.syex.sample


/**
 * An exemplary *view* from the MVP architecture.
 */
interface HelloWordView {

    /**
     * Sets the given *text* to the hello world *TextView*.
     */
    fun setHelloWorldText(text: String)

    fun showFirstError()

    fun clearFirstError()

    fun showSecondError()

    fun clearSecondError()
}