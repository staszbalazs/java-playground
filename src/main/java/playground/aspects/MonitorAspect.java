package playground.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@Aspect
public class MonitorAspect {

    @Pointcut("within(@playground.aspects.Monitor *)")
    public void classAnnotatedWithMonitor() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("@annotation(playground.aspects.Monitor)")
    public void annotatedWithMonitor() {}

    @Pointcut("execution(* *(..))")
    public void method() {}

    @Around("publicMethod() && classAnnotatedWithMonitor()")
    public Object logClassAnnotated(ProceedingJoinPoint point) throws Throwable {
        return log(point);
    }

    @Around("method() && annotatedWithMonitor()")
    public Object logDirectlyAnnotated(ProceedingJoinPoint point) throws Throwable {
        return log(point);
    }

    private Object log(ProceedingJoinPoint point) throws Throwable {
        Method method = getMethod(point);
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        Monitor monitor = getMonitor(method);

        logMessage(
                logger,
                monitor.logLevel(),
                createEntryMessage(method.getName(), monitor.showArgs(), getMethodParams(point), point.getArgs())
        );

        var start = Instant.now();

        var response = point.proceed();

        var end = Instant.now();
        var duration = getDuration(monitor.unit(), start, end);

        logMessage(
                logger,
                monitor.logLevel(),
                createExitMessage(method.getName(), duration, response, monitor.showResult(), monitor.showExecutionTime())
        );

        return response;
    }

    private Monitor getMonitor(Method method) {
        var monitor = method.getAnnotation(Monitor.class);
        if (monitor == null) {
            monitor = method.getDeclaringClass().getAnnotation(Monitor.class);
        }
        return monitor;
    }

    private String getDuration(ChronoUnit unit, Instant start, Instant end) {
        return String.format("%s %s", unit.between(start, end), unit.name().toLowerCase());
    }

    private Method getMethod(ProceedingJoinPoint point) {
        return ((MethodSignature) point.getSignature()).getMethod();
    }

    private String[] getMethodParams(ProceedingJoinPoint point) {
        return ((CodeSignature) point.getSignature()).getParameterNames();
    }

    private String createEntryMessage(String methodName, boolean showArgs, String[] params, Object[] args) {
        StringJoiner message = new StringJoiner(" ")
                .add("Started").add(methodName).add("method");

        if (showArgs && Objects.nonNull(params) && Objects.nonNull(args) && params.length == args.length) {
            Map<String, Object> values = new HashMap<>(params.length);
            for (int i = 0; i < params.length; i++) {
                values.put(params[i], args[i]);
            }
            message.add("with args:")
                    .add(values.toString());
        }
        return message.toString();
    }

    private String createExitMessage(String methodName, String duration, Object result, boolean showResult, boolean showExecutionTime) {
        StringJoiner message = new StringJoiner(" ")
                .add("Finished").add(methodName).add("method");
        if (showExecutionTime) {
            message.add("in").add(duration);
        }
        if (showResult) {
            message.add("with return:").add(result.toString());
        }
        return message.toString();
    }

    private void logMessage(Logger logger, LogLevel level, String message) {
        switch (level) {
            case DEBUG -> logger.debug(message);
            case TRACE -> logger.trace(message);
            case WARN -> logger.warn(message);
            case ERROR, FATAL -> logger.error(message);
            case INFO -> logger.info(message);
        }
    }

}
