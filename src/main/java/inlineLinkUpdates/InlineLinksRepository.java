package inlineLinkUpdates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InlineLinksRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int test() {
        return jdbcTemplate.queryForInt("select 1 from dual");
    }
}

