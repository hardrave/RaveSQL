package jakarta.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Test stand-in for the real jakarta.persistence.Column: proves RaveSQL detects third-party
// column annotations by name without a compile-time JPA dependency.
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Column {
    String name() default "";
}
