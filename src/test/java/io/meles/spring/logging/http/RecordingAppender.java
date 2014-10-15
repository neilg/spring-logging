package io.meles.spring.logging.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

public final class RecordingAppender extends AbstractAppender {

    private final List<LogEvent> loggedEvents = new ArrayList<>();

    protected RecordingAppender(final String name) {
        super(name, null, null, false);
    }

    @Override
    public void append(final LogEvent event) {
        loggedEvents.add(event);
    }

    public List<LogEvent> loggedEvents() {
        return new ArrayList<>(loggedEvents);
    }

    void reset() {
        loggedEvents.clear();
    }

}
