package fwf.clock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class Clock {
    @Inject
    Event<ClockTicked> ticker;

    public void tick() {
        ticker.fire(new ClockTicked());
    }
}
