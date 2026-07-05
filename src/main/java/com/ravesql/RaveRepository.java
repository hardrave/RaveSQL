package com.ravesql;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.ravesql.annotation.SqlPath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🎉 **RaveRepository** 🎉
 *
 * Welcome to the heart-thumping world of {@code RaveRepository}, where SQL queries drop like the sickest beats at a hardcore rave.
 * This repository keeps your data flowing with the relentless energy of a speedcore, ensuring that your applications
 * dance smoothly to the rhythm of your database interactions. 
 *
 * **Hardcore Never Dies!** 🖤
 */
public class RaveRepository {

    /**
     * 💥 **SqlRepositoryException** 💥
     *
     * A custom unchecked exception that serves as the bouncer at your data rave,
     * When things go wrong, this exception keeps the rave spirit alive by handling repository-related errors.
     */
    public static class SqlRepositoryException extends RuntimeException {
        /**
         * Constructs a new {@code SqlRepositoryException} with the specified detail message.
         *
         * @param message the detail message, echoing the woes of a missed beat
         */
        public SqlRepositoryException(String message) {
            super(message);
        }

        /**
         * Constructs a new {@code SqlRepositoryException} with the specified detail message and cause.
         *
         * @param message the detail message, capturing the chaos of a rave gone wild
         * @param cause   the cause of the exception, like a power outage disrupting the flow
         */
        public SqlRepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 🔥 **sqlCache** 🔥
     *
     * This cache stores your SQL queries, keeping them as ready and accessible as your favorite rave anthems.
     * By caching SQL strings, {@code RaveRepository} ensures that your data interactions never miss a beat,
     * maintaining the high-energy flow of your application's backend.
     */
    private final ConcurrentHashMap<String, String> sqlCache = new ConcurrentHashMap<>();

    /**
     * 🎧 **jdbcTemplate** 🎧
     *
     * The {@code NamedParameterJdbcTemplate} acts as the DJ, mixing and spinning your SQL queries
     * with precision and flair. It ensures that each parameter is perfectly aligned,
     * creating a seamless and harmonious execution of your database operations.
     */
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 🛠️ **RaveRepository Constructor** 🛠️
     *
     * Constructs a new {@code RaveRepository} with the provided {@code NamedParameterJdbcTemplate}.
     * This template is the backbone of your data interactions, orchestrating the flawless execution
     * of your SQL queries in true rave fashion.
     *
     * @param jdbcTemplate the JDBC template that serves as the DJ, spinning SQL queries
     */
    public RaveRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 🔍 **Query Methods** 🔍
     *
     * These methods allow you to query the database, retrieving lists of objects
     * that match the rhythm of your SQL queries. Whether you're dropping key-value pairs
     * or grooving with parameter objects, {@code RaveRepository} keeps the data flowing.
     *
     * @param <T>      the type of the objects to return, dancing to the beat of your SQL
     * @param type     the class of the objects to return
     * @param keyValues key-value pairs representing query parameters
     * @return a list of objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> List<T> query(Class<T> type, Object... keyValues) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, keyValues);
    }

    /**
     * 🎶 **Query with Parameters** 🎶
     *
     * Executes a SQL query with the given parameters, retrieving a list of objects
     * that match the specified type. It's like syncing your beats with the perfect melody.
     *
     * @param <T>    the type of the objects to return
     * @param type   the class of the objects to return
     * @param params the parameters for the SQL query
     * @return a list of objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> List<T> query(Class<T> type, Object params) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, params);
    }

    /**
     * 🎤 **Simple Query** 🎤
     *
     * Executes a SQL query without any parameters, retrieving all objects of the specified type.
     * Perfect for when you want to let the data flow without any constraints.
     *
     * @param <T>  the type of the objects to return
     * @param type the class of the objects to return
     * @return a list of all objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public <T> List<T> query(Class<T> type) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, null);
    }

    /**
     * 🔥 **Raw Query Methods** 🔥
     *
     * These methods allow you to execute raw SQL queries by specifying the SQL path directly.
     * It's like dropping your own beats, giving you full control over the data flow.
     *
     * @param <T>      the type of the objects to return
     * @param sqlPath  the path to the SQL file
     * @param type     the class of the objects to return
     * @param keyValues key-value pairs representing query parameters
     * @return a list of objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> List<T> rawQuery(String sqlPath, Class<T> type, Object... keyValues) {
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, keyValues);
    }

    /**
     * 🎧 **Raw Query with Parameters** 🎧
     *
     * Executes a raw SQL query with the given parameters, retrieving a list of objects
     * that match the specified type.
     *
     * @param <T>    the type of the objects to return
     * @param sqlPath the path to the SQL file
     * @param type   the class of the objects to return
     * @param params the parameters for the SQL query
     * @return a list of objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> List<T> rawQuery(String sqlPath, Class<T> type, Object params) {
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, params);
    }

    /**
     * 🎵 **Simple Raw Query** 🎵
     *
     * Executes a raw SQL query without any parameters, retrieving all objects of the specified type.
     *
     * @param <T>     the type of the objects to return
     * @param sqlPath the path to the SQL file
     * @param type    the class of the objects to return
     * @return a list of all objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public <T> List<T> rawQuery(String sqlPath, Class<T> type) {
        String sql = readSqlFromFile(sqlPath);
        return queryInternal(sql, type, null);
    }

    /**
     * 🌟 **Query for Single Object** 🌟
     *
     * Retrieves a single object from the database that matches the query parameters.
     * Perfect for when you need to spotlight a specific record in the data rave.
     *
     * @param <T>      the type of the object to return
     * @param type     the class of the object to return
     * @param keyValues key-value pairs representing query parameters
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> T queryForObject(Class<T> type, Object... keyValues) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, keyValues);
    }

    /**
     * 🎯 **Query for Single Object with Parameters** 🎯
     *
     * Retrieves a single object from the database based on the provided parameters.
     *
     * @param <T>    the type of the object to return
     * @param type   the class of the object to return
     * @param params the parameters for the SQL query
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> T queryForObject(Class<T> type, Object params) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, params);
    }

    /**
     * 🕺 **Simple Query for Single Object** 🕺
     *
     * Retrieves a single object from the database without any parameters.
     *
     * @param <T>  the type of the object to return
     * @param type the class of the object to return
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public <T> T queryForObject(Class<T> type) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, null);
    }

    /**
     * 🎛️ **Raw Query for Single Object** 🎛️
     *
     * Executes a raw SQL query to retrieve a single object, giving you full control over the data selection.
     *
     * @param <T>      the type of the object to return
     * @param sqlPath  the path to the SQL file
     * @param type     the class of the object to return
     * @param keyValues key-value pairs representing query parameters
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> T rawQueryForObject(String sqlPath, Class<T> type, Object... keyValues) {
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, keyValues);
    }

    /**
     * 🎚️ **Raw Query for Single Object with Parameters** 🎚️
     *
     * Executes a raw SQL query with parameters to retrieve a single object.
     *
     * @param <T>    the type of the object to return
     * @param sqlPath the path to the SQL file
     * @param type   the class of the object to return
     * @param params the parameters for the SQL query
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public <T> T rawQueryForObject(String sqlPath, Class<T> type, Object params) {
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, params);
    }

    /**
     * 🎤 **Simple Raw Query for Single Object** 🎤
     *
     * Executes a raw SQL query without parameters to retrieve a single object.
     *
     * @param <T>     the type of the object to return
     * @param sqlPath the path to the SQL file
     * @param type    the class of the object to return
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public <T> T rawQueryForObject(String sqlPath, Class<T> type) {
        String sql = readSqlFromFile(sqlPath);
        return queryForObjectInternal(sql, type, null);
    }

    /**
     * 🔄 **Update Methods** 🔄
     *
     * These methods allow you to perform update operations on the database,
     * modifying records in sync with your application's rhythm.
     *
     * @param keyValues key-value pairs representing update parameters
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int update(Object... keyValues) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, keyValues);
    }

    /**
     * 🔧 **Update with Parameters** 🔧
     *
     * Executes an update operation with the provided parameters.
     *
     * @param params the parameters for the update query
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int update(Object params) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, params);
    }

    /**
     * 🌐 **Simple Update** 🌐
     *
     * Executes an update operation without any parameters.
     *
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public int update() {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, null);
    }

    /**
     * 🔥 **Raw Update Methods** 🔥
     *
     * These methods allow you to perform raw update operations by specifying the SQL path directly.
     *
     * @param sqlPath  the path to the SQL file
     * @param keyValues key-value pairs representing update parameters
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int rawUpdate(String sqlPath, Object... keyValues) {
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, keyValues);
    }

    /**
     * 🔩 **Raw Update with Parameters** 🔩
     *
     * Executes a raw update operation with the provided parameters.
     *
     * @param sqlPath the path to the SQL file
     * @param params  the parameters for the update query
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int rawUpdate(String sqlPath, Object params) {
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, params);
    }

    /**
     * 🎚️ **Simple Raw Update** 🎚️
     *
     * Executes a raw update operation without any parameters.
     *
     * @param sqlPath the path to the SQL file
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query
     */
    public int rawUpdate(String sqlPath) {
        String sql = readSqlFromFile(sqlPath);
        return updateInternal(sql, null);
    }

    /**
     * 🎉 **Batch Update** 🎉
     *
     * Performs a batch update with a list of parameter objects, allowing you to modify multiple records
     * in one synchronized drop. It's like unleashing a barrage of beats that keep the rave energized.
     *
     * @param paramObjects the list of parameter objects for each update
     * @return an array indicating the number of rows affected for each update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int[] batchUpdate(List<?> paramObjects) {
        String sqlPath = getSqlPathFromCaller();
        String sql = readSqlFromFile(sqlPath);
        SqlParameterSource[] batchParams = paramObjects.stream()
                .map(ColumnMappings::beanParameterSource)
                .toArray(SqlParameterSource[]::new);
        return jdbcTemplate.batchUpdate(sql, batchParams);
    }

    /**
     * 🔁 **Raw Batch Update** 🔁
     *
     * Executes a raw batch update with a list of parameter objects, giving you the power to modify multiple records
     * with precise control over each drop.
     *
     * @param sqlPath      the path to the SQL file
     * @param paramObjects the list of parameter objects for each update
     * @return an array indicating the number of rows affected for each update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    public int[] rawBatchUpdate(String sqlPath, List<?> paramObjects) {
        String sql = readSqlFromFile(sqlPath);
        SqlParameterSource[] batchParams = paramObjects.stream()
                .map(ColumnMappings::beanParameterSource)
                .toArray(SqlParameterSource[]::new);
        return jdbcTemplate.batchUpdate(sql, batchParams);
    }

    /**
     * 🚀 **Preload SQL Queries** 🚀
     *
     * Preloads a list of SQL queries into the cache, ensuring that your favorite tracks are always ready
     * to be played without any delay. Perfect for setting up the vibe before the rave starts.
     *
     * @param sqlPaths the list of SQL file paths to preload
     * @throws SqlRepositoryException if there's an issue preloading any of the SQL queries
     */
    public void preloadSqlQueries(List<String> sqlPaths) {
        sqlPaths.forEach(sqlPath -> {
            try {
                readSqlFromFile(sqlPath);
            } catch (SqlRepositoryException e) {
                throw new SqlRepositoryException("Failed to preload SQL for path: " + sqlPath, e);
            }
        });
    }

    /**
     * 🧹 **Clear SQL Cache** 🧹
     *
     * Clears the SQL cache, allowing you to refresh your playlist and ensure that the latest queries
     * are always in sync with your application's needs.
     */
    public void clearSqlCache() {
        sqlCache.clear();
    }

    // ====================================================================================
    //                                PRIVATE HELPER METHODS
    // ====================================================================================

    /**
     * 🎤 **readSqlFromFile** 🎤
     *
     * Reads an SQL query from a file, caching it for future use. Ensures that your SQL tracks
     * are always ready to drop without any lag.
     *
     * @param sqlPath the path to the SQL file within the rave venue
     * @return the SQL string ready to be executed
     * @throws SqlRepositoryException if the SQL file is not found or cannot be read
     */
    private String readSqlFromFile(String sqlPath) {
        // Check if SQL is already cached
        return sqlCache.computeIfAbsent(sqlPath, path -> {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    throw new SqlRepositoryException("SQL file not found: " + path);
                }
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                throw new SqlRepositoryException("SQL file not found: " + path, e);
            } catch (IOException e) {
                throw new SqlRepositoryException("Error reading SQL file: " + path, e);
            }
        });
    }

    /**
     * 🔍 **getSqlPathFromCaller** 🔍
     *
     * Retrieves the SQL path from the calling method's {@code @SqlPath} annotation.
     * Ensures that the right track is played based on the caller's vibe.
     *
     * @return the path to the SQL file as specified in the {@code @SqlPath} annotation
     * @throws SqlRepositoryException if no {@code @SqlPath} annotation is found or retrieval fails
     */
    private String getSqlPathFromCaller() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .map(RaveRepository::resolveCallerMethod)
                        .filter(method -> method != null && method.isAnnotationPresent(SqlPath.class))
                        .findFirst()
                        .map(method -> method.getAnnotation(SqlPath.class).value())
                        .orElseThrow(() -> new SqlRepositoryException("No @SqlPath annotation found on calling method."))
                );
    }

    /**
     * Resolves the exact {@link Method} behind a stack frame, using the frame's
     * {@code MethodType} so that parameterized and overloaded methods are matched correctly.
     *
     * @param frame the stack frame to resolve
     * @return the resolved method, or {@code null} if it cannot be resolved (e.g. synthetic frames)
     */
    private static Method resolveCallerMethod(StackWalker.StackFrame frame) {
        try {
            return frame.getDeclaringClass()
                    .getDeclaredMethod(frame.getMethodName(), frame.getMethodType().parameterArray());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 🎛️ **buildSqlParameterSource** 🎛️
     *
     * Constructs a {@code SqlParameterSource} from various input types, ensuring that your parameters
     * are perfectly mixed for the SQL query.
     *
     * @param params the parameters for the SQL query
     * @return a {@code SqlParameterSource} ready to be used in the query
     * @throws IllegalArgumentException if key-values are not in pairs
     */
    private SqlParameterSource buildSqlParameterSource(Object params) {
        if (params == null) {
            return new MapSqlParameterSource();
        } else if (params instanceof SqlParameterSource) {
            return (SqlParameterSource) params;
        } else if (params instanceof Map) {
            return new MapSqlParameterSource((Map<String, ?>) params);
        } else if (params instanceof Object[]) {
            Object[] keyValues = (Object[]) params;
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            if (keyValues.length % 2 != 0) {
                throw new IllegalArgumentException("Key-values must be in pairs.");
            }
            for (int i = 0; i < keyValues.length; i += 2) {
                paramSource.addValue((String) keyValues[i], keyValues[i + 1]);
            }
            return paramSource;
        } else {
            return ColumnMappings.beanParameterSource(params);
        }
    }

    /**
     * 🎶 **queryInternal** 🎶
     *
     * The core method that executes SQL queries and maps the results to objects.
     * Ensures that every query hits the dancefloor with precision.
     *
     * @param <T>    the type of the objects to return
     * @param sql    the SQL query to execute
     * @param type   the class of the objects to return
     * @param params the parameters for the SQL query
     * @return a list of objects retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    private <T> List<T> queryInternal(String sql, Class<T> type, Object params) {
        SqlParameterSource paramSource = buildSqlParameterSource(params);
        RowMapper<T> rowMapper = ColumnMappings.rowMapper(type);
        return jdbcTemplate.query(sql, paramSource, rowMapper);
    }

    /**
     * 🎯 **queryForObjectInternal** 🎯
     *
     * Executes a SQL query to retrieve a single object, ensuring that the spotlight hits the right target.
     *
     * @param <T>    the type of the object to return
     * @param sql    the SQL query to execute
     * @param type   the class of the object to return
     * @param params the parameters for the SQL query
     * @return the single object retrieved from the database
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    private <T> T queryForObjectInternal(String sql, Class<T> type, Object params) {
        SqlParameterSource paramSource = buildSqlParameterSource(params);
        RowMapper<T> rowMapper = ColumnMappings.rowMapper(type);
        return jdbcTemplate.queryForObject(sql, paramSource, rowMapper);
    }

    /**
     * 🔄 **updateInternal** 🔄
     *
     * Executes an update operation with the given SQL and parameters, ensuring that your data stays in sync
     * with the high-energy flow of your application.
     *
     * @param sql    the SQL update query to execute
     * @param params the parameters for the update query
     * @return the number of rows affected by the update
     * @throws SqlRepositoryException if there's an issue with the SQL query or parameters
     */
    private int updateInternal(String sql, Object params) {
        SqlParameterSource paramSource = buildSqlParameterSource(params);
        return jdbcTemplate.update(sql, paramSource);
    }
}
