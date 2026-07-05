package com.ravesql;

import com.ravesql.annotation.Column;
import com.ravesql.annotation.SqlPath;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnAnnotationTest {

    private RaveRepository repository;

    @BeforeEach
    public void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:columndb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        repository = new RaveRepository(jdbcTemplate);

        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS mail_table;");
        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE mail_table (id INT PRIMARY KEY, mail_from VARCHAR(255));");
        jdbcTemplate.getJdbcTemplate().execute("INSERT INTO mail_table (id, mail_from) VALUES (1, 'alice@rave.com');");
    }

    // Entity using RaveSQL's own @Column on a field
    public static class Mail {
        private int id;

        @Column(name = "mail_from")
        private String mailFrom;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getMailFrom() { return mailFrom; }
        public void setMailFrom(String mailFrom) { this.mailFrom = mailFrom; }
    }

    // Entity using the (test stand-in) jakarta.persistence.Column, as a JPA-annotated class would
    public static class JakartaMail {
        private int id;

        @jakarta.persistence.Column(name = "mail_from")
        private String mailFrom;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getMailFrom() { return mailFrom; }
        public void setMailFrom(String mailFrom) { this.mailFrom = mailFrom; }
    }

    // Entity with @Column on the getter instead of the field
    public static class GetterMail {
        private int id;
        private String mailFrom;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        @Column(name = "mail_from")
        public String getMailFrom() { return mailFrom; }
        public void setMailFrom(String mailFrom) { this.mailFrom = mailFrom; }
    }

    // Read side: annotated column maps into the differently-named property
    @Test
    public void testQueryMapsAnnotatedColumn() {
        Mail mail = repository.rawQueryForObject("sql/mail_select_by_id.sql", Mail.class, "id", 1);
        assertNotNull(mail);
        assertEquals(1, mail.getId());
        assertEquals("alice@rave.com", mail.getMailFrom());
    }

    // Write side: :mail_from named parameter binds the mailFrom property
    @Test
    public void testUpdateBindsAnnotatedColumn() {
        Mail mail = new Mail();
        mail.setId(2);
        mail.setMailFrom("bob@rave.com");

        int rowsAffected = repository.rawUpdate("sql/mail_insert.sql", mail);
        assertEquals(1, rowsAffected);

        Mail saved = repository.rawQueryForObject("sql/mail_select_by_id.sql", Mail.class, "id", 2);
        assertEquals("bob@rave.com", saved.getMailFrom());
    }

    // Batch write side: annotated binding works for each element
    @Test
    public void testBatchUpdateBindsAnnotatedColumn() {
        Mail dave = new Mail();
        dave.setId(3);
        dave.setMailFrom("dave@rave.com");
        Mail eve = new Mail();
        eve.setId(4);
        eve.setMailFrom("eve@rave.com");

        int[] rowsAffected = repository.rawBatchUpdate("sql/mail_insert.sql", Arrays.asList(dave, eve));
        assertArrayEquals(new int[]{1, 1}, rowsAffected);

        List<Mail> all = repository.rawQuery("sql/mail_select_all.sql", Mail.class);
        assertEquals(3, all.size());
        assertEquals("eve@rave.com", all.get(2).getMailFrom());
    }

    // Third-party column annotations (jakarta.persistence.Column) are recognized without a JPA dependency
    @Test
    public void testJakartaColumnAnnotationRecognized() {
        JakartaMail mail = repository.rawQueryForObject("sql/mail_select_by_id.sql", JakartaMail.class, "id", 1);
        assertEquals("alice@rave.com", mail.getMailFrom());

        JakartaMail carol = new JakartaMail();
        carol.setId(5);
        carol.setMailFrom("carol@rave.com");
        assertEquals(1, repository.rawUpdate("sql/mail_insert.sql", carol));
    }

    // @Column on the getter works the same as on the field
    @Test
    public void testColumnAnnotationOnGetter() {
        GetterMail mail = repository.rawQueryForObject("sql/mail_select_by_id.sql", GetterMail.class, "id", 1);
        assertEquals("alice@rave.com", mail.getMailFrom());
    }

    // Annotated mapping also works through the @SqlPath stack-walking path, not just raw* methods
    @Test
    @SqlPath("sql/mail_select_by_id.sql")
    public void testColumnMappingViaSqlPath() {
        Mail mail = repository.queryForObject(Mail.class, "id", 1);
        assertEquals("alice@rave.com", mail.getMailFrom());
    }
}
