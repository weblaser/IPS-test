package com.ctl.security.ips.api.jersey;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by kevin.weber on 1/5/2015.
 */
class JerseyLog4jLoggerWrapper extends Handler {

    private static Logger logger = LogManager.getLogger(JerseyLog4jLoggerWrapper.class);

    @Override
    public void publish(LogRecord record) {
        String sourceClassName = record.getSourceClassName();
        Level level = record.getLevel();

        if (sourceClassName != null && (level.intValue() >= Level.ALL.intValue() || sourceClassName.contains("org.glassfish.jersey"))) {
            Logger sourceLogger = LogManager.getLogger(sourceClassName);
            sourceLogger.log(convertToLog4JPriority(level), record.getMessage());
        }
    }

    private org.apache.logging.log4j.Level convertToLog4JPriority(Level level) {
        if (Level.ALL.equals(level)) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (Level.CONFIG.equals(level)) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (Level.FINE.equals(level)) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (Level.FINER.equals(level)) {
            return org.apache.logging.log4j.Level.DEBUG;
        } else if (Level.FINEST.equals(level)) {
            return org.apache.logging.log4j.Level.DEBUG;
        } else if (Level.INFO.equals(level)) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (Level.SEVERE.equals(level)) {
            return org.apache.logging.log4j.Level.ERROR;
        } else if (Level.WARNING.equals(level)) {
            return org.apache.logging.log4j.Level.WARN;
        } else {
            return org.apache.logging.log4j.Level.INFO;
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
