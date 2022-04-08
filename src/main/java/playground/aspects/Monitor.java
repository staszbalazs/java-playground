package playground.aspects;


import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Monitor {

    LogLevel logLevel() default LogLevel.INFO;
    ChronoUnit unit() default ChronoUnit.MILLIS;
    boolean showArgs() default false;
    boolean showResult() default false;
    boolean showExecutionTime() default true;

}
