package io.meles.spring.logging.http;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.hamcrest.Matcher;

public class LogEventMatchers {

    public static Matcher<LogEvent> level(final Level level) {
        return hasProperty("level", equalTo(level));
    }

    public static Matcher<LogEvent> message(final String message) {
        return hasProperty("message", hasProperty("formattedMessage", equalTo(message)));
    }


}
