package com.ravesql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🎛️ **Column Annotation** 🎛️
 *
 * Maps an entity field (or its getter) to a database column whose name doesn't match the property name —
 * like giving your track an alias for the setlist. With {@code @Column}, {@code mail_from} in the database
 * and {@code mailFrom} in your entity stay perfectly in sync, both when reading results and when binding
 * named parameters.
 *
 * <pre>{@code
 * public class Mail {
 *     private int id;
 *
 *     @Column(name = "mail_from")
 *     private String mailFrom;
 * }
 * }</pre>
 *
 * RaveSQL also recognizes {@code jakarta.persistence.Column} and {@code javax.persistence.Column}
 * on your entities, so JPA-annotated classes join the rave without any changes. When both are present,
 * this annotation takes precedence.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Column {

    /**
     * 🎶 **Column Name** 🎶
     *
     * The name of the database column this property maps to.
     *
     * @return the column name as it appears in SQL
     */
    String name();
}
