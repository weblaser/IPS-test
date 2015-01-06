package com.ctl.security.ips.api.jersey;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JerseyLog4jLoggerWrapperTest {

    private static final String SOURCE_CLASS = "sourceClass";
    JerseyLog4jLoggerWrapper underTest = new JerseyLog4jLoggerWrapper();

    @Test
    public void testPublishAll() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.ALL, "some message");
        record.setSourceClassName(SOURCE_CLASS);
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishNull() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.ALL, null);
        record.setSourceClassName(SOURCE_CLASS);
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishGlassfish() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.ALL, "org.glassfish.jersey");
        record.setSourceClassName(SOURCE_CLASS);
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishConfig() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.CONFIG, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishFine() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.FINE, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishFiner() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.FINER, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishFinest() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.FINEST, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishInfo() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.INFO, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishSevere() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.SEVERE, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishWarning() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.WARNING, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testPublishElse() throws Exception {
        //arrange
        LogRecord record = new LogRecord(Level.OFF, "some message");
        record.setSourceClassName("sourceClass");
        //act
        underTest.publish(record);
    }

    @Test
    public void testFlush() throws Exception {
        underTest.flush();
    }

    @Test
    public void testClose() throws Exception {
        underTest.close();
    }
}