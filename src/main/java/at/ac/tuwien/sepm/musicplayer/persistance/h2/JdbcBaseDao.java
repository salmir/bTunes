package at.ac.tuwien.sepm.musicplayer.persistance.h2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Base DAO for all other DAO-implementations
 *
 * Created by Lena Lenz.
 */
@Component
public abstract class JdbcBaseDao {

    protected static JdbcTemplate jdbcTemplate;

    @Autowired(required=true)
    public void init(DataSource dataSource) {
        if(jdbcTemplate == null) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
