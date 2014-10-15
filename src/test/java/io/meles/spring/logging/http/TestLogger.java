package io.meles.spring.logging.http;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestLogger implements TestRule {

    private final String logger;

    private RecordingAppender recordingAppender;

    public TestLogger(final String logger) {
        this.logger = logger;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                final LoggerContext context = loggerContext();
                final Configuration configuration = context.getConfiguration();

                if (!configuration.getLoggers().containsKey(logger)) {
                    final LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, logger, "true", new AppenderRef[0], null, configuration, null);
                    configuration.addLogger(logger, loggerConfig);
                }

                final LoggerConfig loggerConfig = configuration.getLoggerConfig(logger);

                final String appenderName = description.getDisplayName();
                recordingAppender = new RecordingAppender(appenderName);
                recordingAppender.start();
                configuration.addAppender(recordingAppender);

                loggerConfig.addAppender(recordingAppender, null, null);

                context.updateLoggers();

                try {
                    base.evaluate();
                } finally {
                    reset();
                    loggerConfig.removeAppender(appenderName);
                    context.updateLoggers();
                }

            }
        };
    }

    private LoggerContext loggerContext() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        return context;
    }

    public List<LogEvent> loggedEvents() {
        return recordingAppender.loggedEvents();
    }

    public void setLevel(final Level level) {
        final LoggerContext context = loggerContext();
        final Configuration configuration = context.getConfiguration();
        final LoggerConfig loggerConfig = configuration.getLoggerConfig(logger);
        loggerConfig.setLevel(level);
        context.updateLoggers();
    }

    public void reset() {
        setLevel(Level.ALL);
        recordingAppender.reset();
    }
}
