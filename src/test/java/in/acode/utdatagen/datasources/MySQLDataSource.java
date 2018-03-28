package in.acode.utdatagen.datasources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class MySQLDataSource {

    @Value("${mysql.db.url}")
    private String url;

    @Value("${mysql.db.username}")
    private String username;

    @Value("${mysql.db.password}")
    private String password;

    @Bean("mysql-data-source")
    @Primary
    public DataSource mySqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        return dataSource;
    }

    @Bean("mysql-jdbc-template")
    public JdbcTemplate mySqlJdbcTemplate() {
        return new JdbcTemplate(mySqlDataSource());
    }

}
