package com.ravesql;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🎚️ **ColumnMappings** 🎚️
 *
 * The backstage crew that keeps entity properties and database columns dancing in sync.
 * Scans entity classes once for column-name annotations, caches the result, and hands out
 * column-aware row mappers and parameter sources so that {@code @Column(name = "mail_from")}
 * works in both directions: reading query results and binding named parameters.
 *
 * Annotations are detected by fully qualified name rather than by class, so entities annotated
 * with {@code jakarta.persistence.Column} or {@code javax.persistence.Column} work without
 * RaveSQL depending on JPA. RaveSQL's own {@link com.ravesql.annotation.Column} takes precedence.
 */
final class ColumnMappings {

    /**
     * Recognized column annotations, in precedence order. Matched by fully qualified name
     * so none of them (beyond RaveSQL's own) need to be on the compile-time classpath.
     */
    private static final List<String> COLUMN_ANNOTATION_TYPES = List.of(
            "com.ravesql.annotation.Column",
            "jakarta.persistence.Column",
            "javax.persistence.Column");

    /** Per-entity-class cache of column name (lowercase) → property name. */
    private static final ConcurrentHashMap<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private ColumnMappings() {
    }

    /**
     * Returns a {@link RowMapper} for the given type. Classes without column annotations get
     * Spring's stock {@link BeanPropertyRowMapper}; annotated classes get a mapper that applies
     * the annotation overrides first and falls back to underscore-to-camelCase matching.
     *
     * @param <T>  the entity type
     * @param type the entity class
     * @return a row mapper honoring column-name annotations
     */
    static <T> RowMapper<T> rowMapper(Class<T> type) {
        Map<String, String> overrides = columnToProperty(type);
        return overrides.isEmpty()
                ? BeanPropertyRowMapper.newInstance(type)
                : new ColumnAwareRowMapper<>(type, overrides);
    }

    /**
     * Returns a {@link SqlParameterSource} for the given bean. Beans without column annotations
     * get Spring's stock {@link BeanPropertySqlParameterSource}; annotated beans get a source that
     * also answers to the annotated column names (e.g. {@code :mail_from} → {@code mailFrom}).
     *
     * @param bean the parameter object
     * @return a parameter source honoring column-name annotations
     */
    static SqlParameterSource beanParameterSource(Object bean) {
        Map<String, String> overrides = columnToProperty(bean.getClass());
        BeanPropertySqlParameterSource delegate = new BeanPropertySqlParameterSource(bean);
        return overrides.isEmpty() ? delegate : new ColumnAwareParameterSource(delegate, overrides);
    }

    private static Map<String, String> columnToProperty(Class<?> type) {
        return CACHE.computeIfAbsent(type, ColumnMappings::scan);
    }

    private static Map<String, String> scan(Class<?> type) {
        Map<String, String> mappings = new HashMap<>();
        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(type)) {
            String columnName = columnNameFor(findField(type, pd.getName()));
            if (columnName == null) {
                columnName = columnNameFor(pd.getReadMethod());
            }
            if (columnName != null) {
                mappings.put(columnName.toLowerCase(), pd.getName());
            }
        }
        return Map.copyOf(mappings);
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // keep walking up the hierarchy
            }
        }
        return null;
    }

    private static String columnNameFor(AnnotatedElement element) {
        if (element == null) {
            return null;
        }
        for (String annotationType : COLUMN_ANNOTATION_TYPES) {
            for (Annotation annotation : element.getAnnotations()) {
                if (annotation.annotationType().getName().equals(annotationType)) {
                    String name = readNameAttribute(annotation);
                    if (name != null && !name.isEmpty()) {
                        return name;
                    }
                }
            }
        }
        return null;
    }

    private static String readNameAttribute(Annotation annotation) {
        try {
            return (String) annotation.annotationType().getMethod("name").invoke(annotation);
        } catch (ReflectiveOperationException | ClassCastException e) {
            // an unrelated annotation that happens to share a recognized type name; ignore it
            return null;
        }
    }

    /**
     * Maps result-set columns to bean properties, consulting the annotation overrides first
     * and falling back to the same underscore-to-camelCase convention as {@link BeanPropertyRowMapper}.
     */
    private static final class ColumnAwareRowMapper<T> implements RowMapper<T> {

        private final Class<T> type;
        private final Map<String, String> columnToProperty;

        private ColumnAwareRowMapper(Class<T> type, Map<String, String> columnToProperty) {
            this.type = type;
            this.columnToProperty = columnToProperty;
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            T instance = BeanUtils.instantiateClass(type);
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(instance);
            ResultSetMetaData metaData = rs.getMetaData();
            for (int index = 1; index <= metaData.getColumnCount(); index++) {
                String column = JdbcUtils.lookupColumnName(metaData, index);
                String property = columnToProperty.get(column.toLowerCase());
                if (property == null) {
                    property = JdbcUtils.convertUnderscoreNameToPropertyName(column);
                }
                if (beanWrapper.isWritableProperty(property)) {
                    Object value = JdbcUtils.getResultSetValue(rs, index, beanWrapper.getPropertyType(property));
                    beanWrapper.setPropertyValue(property, value);
                }
            }
            return instance;
        }
    }

    /**
     * Lets named parameters address bean properties by their annotated column names,
     * so {@code :mail_from} binds the {@code mailFrom} property.
     */
    private static final class ColumnAwareParameterSource extends AbstractSqlParameterSource {

        private final BeanPropertySqlParameterSource delegate;
        private final Map<String, String> columnToProperty;

        private ColumnAwareParameterSource(BeanPropertySqlParameterSource delegate,
                                           Map<String, String> columnToProperty) {
            this.delegate = delegate;
            this.columnToProperty = columnToProperty;
        }

        @Override
        public boolean hasValue(String paramName) {
            return delegate.hasValue(translate(paramName));
        }

        @Override
        public Object getValue(String paramName) {
            return delegate.getValue(translate(paramName));
        }

        @Override
        public int getSqlType(String paramName) {
            return delegate.getSqlType(translate(paramName));
        }

        @Override
        public String getTypeName(String paramName) {
            return delegate.getTypeName(translate(paramName));
        }

        private String translate(String paramName) {
            String property = columnToProperty.get(paramName.toLowerCase());
            return property != null ? property : paramName;
        }
    }
}
