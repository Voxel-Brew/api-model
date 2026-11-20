package dev.voxelbrew.api.multithread;

import java.util.concurrent.ExecutorService;

/** Supplies the host-owned executor shared across the application. */
public interface MultithreadService extends AutoCloseable {
    /**
     * Returns the shared executor sized according to the host's user preferences.
     * Consumers may submit work and cancel their own tasks. The host owns shutdown.
     *
     * @return the same executor for the lifetime of the application
     */
    ExecutorService commonExecutorService();

    /** Called by the host at application shutdown to close the shared executor and await termination. */
    @Override
    default void close() {
        commonExecutorService().close();
    }
}
