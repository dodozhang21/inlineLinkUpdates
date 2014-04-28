package utils;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SyncMdpTopicContext {
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=dwebdb1dr)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=dwebdb2dr)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=dweb)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))))");
        dataSource.setUsername("dynamo_dev");
        dataSource.setPassword("dynamo_dev");

        return dataSource;
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource prodDataSource() {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=pwebdb1dsm)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=pwebdb2dsm)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=pwebdb3dsm)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=pwebdb4dsm)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=pwebdb5dsm)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=papp.meredith.com)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))");
        dataSource.setUsername("yourUsername");
        dataSource.setPassword("yourPassword");

        return dataSource;
    }

    @Bean
    public JdbcTemplate prodJdbcTemplate() {
        return new JdbcTemplate(prodDataSource());
    }
}
