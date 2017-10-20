package de.syex.rode;

/**
 * An action to be executed by a *view*.
 */
public interface ViewCommand<View> {

    void execute(View view);
}
