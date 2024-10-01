package com.ravesql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * 🌟 **SqlPath Annotation** 🌟
 *
 * Welcome to the pulsating core of {@code SqlPath}, the annotation that ensures your SQL queries are always in sync
 * with the relentless beats of your application's data flow. Just like a DJ drops the perfect track at the peak
 * of the rave, {@code @SqlPath} links your methods to their corresponding SQL scripts, keeping the data rave alive.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SqlPath {
    
    /**
     * 🎶 **SQL Path Value** 🎶
     *
     * Specifies the path to the SQL file that accompanies the annotated method. Think of it as the tracklist
     * that keeps the rave going, ensuring every method knows exactly which SQL script to execute.
     *
     * @return the relative path to the SQL file, ready to drop like the sickest beat
     */
    String value();
}
