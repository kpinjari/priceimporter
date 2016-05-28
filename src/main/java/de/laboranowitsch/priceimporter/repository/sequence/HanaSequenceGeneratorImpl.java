package de.laboranowitsch.priceimporter.repository.sequence;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Hana {@link SequenceGenerator} implementation.
 *
 * @author christian@laboranowitsch.de
 */
public class HanaSequenceGeneratorImpl implements SequenceGenerator {

    private final static String SEQ_STMT = "select SEQ_NAME.nextval from dummy";
    private final JdbcTemplate jdbcTemplate;

    public HanaSequenceGeneratorImpl(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Long getNextSequence(String sequenceName) {
        String stmt = SEQ_STMT.replace("SEQ_NAME", sequenceName);
        return jdbcTemplate.queryForObject(stmt, Long.class);
    }
}
