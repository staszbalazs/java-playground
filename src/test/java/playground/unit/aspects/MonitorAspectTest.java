package playground.unit.aspects;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import playground.aspects.Monitor;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonitorAspectTest {

    private static MethodAnnotatedMonitorTestClass methodAnnotatedMonitorTestClass;
    private static ClassAnnotatedMonitorTestClass classAnnotatedMonitorTestClass;
    private static ListAppender<ILoggingEvent> listAppender;

    private static final String INPUT = "Input";

    private static final String START_MESSAGE_METHOD_ANNOTATED_DEBUG = "Started logWithDebugAndMinutes method with args: {text=" + INPUT + "}";
    private static final String FINISH_MESSAGE_START_METHOD_ANNOTATED_DEBUG = "Finished logWithDebugAndMinutes method in ";
    private static final String FINISH_MESSAGE_END_METHOD_ANNOTATED_DEBUG = " minutes with return: " + INPUT + " - debug/minutes";

    private static final String START_MESSAGE_METHOD_ANNOTATED_ERROR = "Started logWithErrorAndMillisPrivate method with args: {text=" + INPUT + "}";
    private static final String FINISH_MESSAGE_START_METHOD_ANNOTATED_ERROR = "Finished logWithErrorAndMillisPrivate method in ";
    private static final String FINISH_MESSAGE_END_METHOD_ANNOTATED_ERROR = " millis with return: " + INPUT + " - error/millis";

    private static final String START_MESSAGE_CLASS_ANNOTATED_PUBLIC = "Started publicMethod method";
    private static final String FINISH_MESSAGE_CLASS_ANNOTATED_PUBLIC = "Finished publicMethod method";

    private static final String START_MESSAGE_CLASS_ANNOTATED_PRIVATE = "Started publicMethodCallingPrivateMethod method";
    private static final String FINISH_MESSAGE_CLASS_ANNOTATED_PRIVATE = "Finished publicMethodCallingPrivateMethod method";

    @BeforeAll
    static void start() {
        methodAnnotatedMonitorTestClass = new MethodAnnotatedMonitorTestClass();
        classAnnotatedMonitorTestClass = new ClassAnnotatedMonitorTestClass();
        listAppender = new ListAppender<>();

        Logger monitorTestLogger = (Logger) LoggerFactory.getLogger(MethodAnnotatedMonitorTestClass.class);
        Logger monitorTest2Logger = (Logger) LoggerFactory.getLogger(ClassAnnotatedMonitorTestClass.class);

        monitorTestLogger.addAppender(listAppender);
        monitorTest2Logger.addAppender(listAppender);
    }

    @BeforeEach
    public void setUp() {
        listAppender.start();
    }

    @AfterEach
    public void tearDown() {
        listAppender.stop();
        listAppender.list.clear();
    }

    @Test
    public void methodIsAnnotatedWithDebugAndMinutes() {

        methodAnnotatedMonitorTestClass.logWithDebugAndMinutes(INPUT);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        assertEquals(START_MESSAGE_METHOD_ANNOTATED_DEBUG, logsList.get(0).getMessage());
        assertEquals(Level.DEBUG, logsList.get(0).getLevel());

        assertTrue(logsList.get(1).getMessage().startsWith(FINISH_MESSAGE_START_METHOD_ANNOTATED_DEBUG));
        assertTrue(logsList.get(1).getMessage().endsWith(FINISH_MESSAGE_END_METHOD_ANNOTATED_DEBUG));
        assertEquals(Level.DEBUG, logsList.get(1).getLevel());

    }

    @Test
    public void privateMethodIsAnnotatedWithErrorAndMillis() {

        methodAnnotatedMonitorTestClass.callLogWithErrorAndMillisPrivate(INPUT);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        assertEquals(START_MESSAGE_METHOD_ANNOTATED_ERROR, logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());

        assertTrue(logsList.get(1).getMessage().startsWith(FINISH_MESSAGE_START_METHOD_ANNOTATED_ERROR));
        assertTrue(logsList.get(1).getMessage().endsWith(FINISH_MESSAGE_END_METHOD_ANNOTATED_ERROR));
        assertEquals(Level.ERROR, logsList.get(1).getLevel());

    }

    @Test
    public void classIsAnnotatedWithShowDetailsFalse() {

        classAnnotatedMonitorTestClass.publicMethod(INPUT);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        assertEquals(START_MESSAGE_CLASS_ANNOTATED_PUBLIC, logsList.get(0).getMessage());
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals(FINISH_MESSAGE_CLASS_ANNOTATED_PUBLIC, logsList.get(1).getMessage());
        assertEquals(Level.INFO, logsList.get(1).getLevel());

    }

    @Test
    public void classIsAnnotatedPrivateMethodNotLogged() {

        classAnnotatedMonitorTestClass.publicMethodCallingPrivateMethod(INPUT);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        assertEquals(START_MESSAGE_CLASS_ANNOTATED_PRIVATE, logsList.get(0).getMessage());
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals(FINISH_MESSAGE_CLASS_ANNOTATED_PRIVATE, logsList.get(1).getMessage());
        assertEquals(Level.INFO, logsList.get(1).getLevel());

    }

    private static class MethodAnnotatedMonitorTestClass {

        @Monitor(logLevel = LogLevel.DEBUG, unit = ChronoUnit.MINUTES, showArgs = true, showResult = true, showExecutionTime = true)
        public String logWithDebugAndMinutes(String text) {
            return text + " - debug/minutes";
        }

        public String callLogWithErrorAndMillisPrivate(String text) {
            return logWithErrorAndMillisPrivate(text);
        }

        @Monitor(logLevel = LogLevel.ERROR, unit = ChronoUnit.MILLIS, showArgs = true, showResult = true, showExecutionTime = true)
        private String logWithErrorAndMillisPrivate(String text) {
            return text + " - error/millis";
        }

    }

    @Monitor(logLevel = LogLevel.INFO, showArgs = false, showResult = false, showExecutionTime = false)
    private static class ClassAnnotatedMonitorTestClass {

        public String publicMethod(String text) {
            return text + " - public method";
        }

        public String publicMethodCallingPrivateMethod(String text) {
            return privateMethodNotAffectedByAspect(text);
        }

        private String privateMethodNotAffectedByAspect(String text) {
            return text + " - public method calling private method";
        }

    }

}

