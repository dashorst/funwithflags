package fwf.app;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;

@ApplicationScoped
public class ConsoleLogger {
    public void observeAllEvents(@Observes Object event) {
        if (event.getClass().getName().startsWith("fwf"))
            Log.infof("Sync Event: %s", event);
    }

    public void observeAllAsyncEvents(@ObservesAsync Object event) {
        if (event.getClass().getName().startsWith("fwf"))
            Log.infof("Async Event: %s", event);
    }
}
