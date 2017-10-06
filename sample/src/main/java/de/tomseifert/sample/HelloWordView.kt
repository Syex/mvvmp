package de.tomseifert.sample


/**
 * An exemplary *view* from the MVP architecture.
 */
interface HelloWordView {

    /**
     * Shows a *Toast* with the given *text*.
     */
    fun showHelloWord(text: String)
}