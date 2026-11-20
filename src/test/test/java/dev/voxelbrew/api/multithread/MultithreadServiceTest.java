package dev.voxelbrew.api.multithread;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class MultithreadServiceTest {
    @Test
    void closingServiceCompletesSubmittedWorkAndTerminatesExecutor() {
        try (var executor = Executors.newSingleThreadExecutor()) {
            var completed = new AtomicBoolean();
            try (MultithreadService service = () -> executor) {
                service.commonExecutorService().execute(() -> completed.set(true));
            }
            assertThat(completed).isTrue();
            assertThat(executor.isTerminated()).isTrue();
        }
    }
}
