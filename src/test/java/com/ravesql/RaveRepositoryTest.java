package com.ravesql;

import com.ravesql.annotation.SqlPath;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RaveRepositoryTest {

    private RaveRepository repository;

    @BeforeEach
    public void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
    
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        repository = new RaveRepository(jdbcTemplate);
    
        // Drop the table if it exists
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS test_table;");
    
        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE test_table (id INT PRIMARY KEY, name VARCHAR(255));");
        jdbcTemplate.getJdbcTemplate().execute("INSERT INTO test_table (id, name) VALUES (1, 'Alice');");
        jdbcTemplate.getJdbcTemplate().execute("INSERT INTO test_table (id, name) VALUES (2, 'Bob');");
    }
    

    // Entity class for mapping results
    public static class TestEntity {
        private int id;
        private String name;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Test querying all records
    @Test
    @SqlPath("sql/select_all.sql")
    public void testQueryAll() throws IOException {
        List<TestEntity> results = repository.query(TestEntity.class);
        assertEquals(2, results.size());
    }

    // Test querying a single record by ID
    @Test
    @SqlPath("sql/select_by_id.sql")
    public void testQueryById() throws IOException {
        TestEntity result = repository.queryForObject(TestEntity.class, "id", 1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Alice", result.getName());
    }

    // Test inserting a new record
    @Test
    @SqlPath("sql/insert.sql")
    public void testInsert() throws IOException {
        int rowsAffected = repository.update("id", 3, "name", "Charlie");
        assertEquals(1, rowsAffected);

        // Verify the data was inserted
        String sqlPath = "sql/select_by_id.sql";
        TestEntity result = repository.rawQueryForObject(sqlPath, TestEntity.class, "id", 3);
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Charlie", result.getName());
    }

    // Test updating an existing record
    @Test
    @SqlPath("sql/update_name.sql")
    public void testUpdate() throws IOException {
        int rowsAffected = repository.update("id", 1, "name", "Alice Updated");
        assertEquals(1, rowsAffected);

        // Verify the data was updated
        String sqlPath = "sql/select_by_id.sql";
        TestEntity result = repository.rawQueryForObject(sqlPath, TestEntity.class, "id", 1);
        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
    }

    // Test deleting a record
    @Test
    @SqlPath("sql/delete_by_id.sql")
    public void testDelete() throws IOException {
        int rowsAffected = repository.update("id", 1);
        assertEquals(1, rowsAffected);

        // Verify the data was deleted
        String sqlPath = "sql/select_all.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class);
        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getId());
    }

    // Test batch updating (inserting multiple records)
    @Test
    @SqlPath("sql/insert.sql")
    public void testBatchUpdate() throws IOException {
        List<TestEntity> entities = Arrays.asList(
                new TestEntity() {{ setId(4); setName("Dave"); }},
                new TestEntity() {{ setId(5); setName("Eve"); }}
        );

        int[] rowsAffected = repository.batchUpdate(entities);
        assertEquals(2, rowsAffected.length);
        assertEquals(1, rowsAffected[0]);
        assertEquals(1, rowsAffected[1]);

        // Verify the data was inserted
        String sqlPath = "sql/select_all.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class);
        assertEquals(4, results.size());
    }

    // Test querying with a Map of parameters
    @Test
    @SqlPath("sql/select_by_name.sql")
    public void testQueryByNameWithMap() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Bob");

        List<TestEntity> results = repository.query(TestEntity.class, params);
        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getName());
    }

    // Test querying with a parameter object
    @Test
    @SqlPath("sql/select_by_name.sql")
    public void testQueryByNameWithObject() throws IOException {
        TestEntity param = new TestEntity();
        param.setName("Alice");

        List<TestEntity> results = repository.query(TestEntity.class, param);
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getName());
    }

    // Test Raw querying all records
    @Test
    
    public void testRawQueryAll() throws IOException {
        String SqlPath = "sql/select_all.sql";
        List<TestEntity> results = repository.rawQuery(SqlPath, TestEntity.class);
        assertEquals(2, results.size());
    }

    // Test Raw querying a single record by ID
    @Test
    public void testRawQueryById() throws IOException {
        String SqlPath = "sql/select_by_id.sql";
        TestEntity result = repository.rawQueryForObject(SqlPath, TestEntity.class, "id", 1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Alice", result.getName());
    }

    // Test inserting a new record
    @Test
    public void testRawInsert() throws IOException {
        String SqlPath = "sql/insert.sql";
        int rowsAffected = repository.rawUpdate(SqlPath, "id", 3, "name", "Charlie");
        assertEquals(1, rowsAffected);

        // Verify the data was inserted
        String sqlPath = "sql/select_by_id.sql";
        TestEntity result = repository.rawQueryForObject(sqlPath, TestEntity.class, "id", 3);
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Charlie", result.getName());
    }

    // Test updating an existing record
    @Test
    public void testRawUpdate() throws IOException {
        String SqlPath = "sql/update_name.sql";
        int rowsAffected = repository.rawUpdate(SqlPath, "id", 1, "name", "Alice Updated");
        assertEquals(1, rowsAffected);

        // Verify the data was updated
        String sqlPath = "sql/select_by_id.sql";
        TestEntity result = repository.rawQueryForObject(sqlPath, TestEntity.class, "id", 1);
        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
    }

    // Test deleting a record
    @Test
    public void testRawDelete() throws IOException {
        String SqlPath = "sql/delete_by_id.sql";
        int rowsAffected = repository.rawUpdate(SqlPath, "id", 1);
        assertEquals(1, rowsAffected);

        // Verify the data was deleted
        String sqlPath = "sql/select_all.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class);
        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getId());
    }

    // Test batch updating (inserting multiple records)
    @Test
    public void testRawBatchUpdate() throws IOException {
        List<TestEntity> entities = Arrays.asList(
                new TestEntity() {{ setId(4); setName("Dave"); }},
                new TestEntity() {{ setId(5); setName("Eve"); }}
        );

        String SqlPath = "sql/insert.sql";
        int[] rowsAffected = repository.rawBatchUpdate(SqlPath, entities);
        assertEquals(2, rowsAffected.length);
        assertEquals(1, rowsAffected[0]);
        assertEquals(1, rowsAffected[1]);

        // Verify the data was inserted
        String sqlPath = "sql/select_all.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class);
        assertEquals(4, results.size());
    }

    // Test querying with a Map of parameters
    @Test
    public void testRawQueryByNameWithMap() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Bob");

        String sqlPath = "sql/select_by_name.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class, params);
        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getName());
    }

    // Test querying with a parameter object
    @Test
    public void testRawQueryByNameWithObject() throws IOException {
        TestEntity param = new TestEntity();
        param.setName("Alice");

        String sqlPath = "sql/select_by_name.sql";
        List<TestEntity> results = repository.rawQuery(sqlPath, TestEntity.class, param);
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getName());
    }

}
