package com.fight.app.data.api;

/**
 * Callback for asynchronous API requests. Invoked on the main thread.
 */
public interface ApiCallback<T> {
    void onSuccess(T data);

    /** @param message human-readable error message, ready to show in a Toast. */
    void onError(String message);
}
