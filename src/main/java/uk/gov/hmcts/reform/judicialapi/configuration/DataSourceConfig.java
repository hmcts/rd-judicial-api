package uk.gov.hmcts.reform.judicialapi.configuration;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.driverClassName}")
    String driverClass;

    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String userName;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.datasource.min-idle}")
    int idleConnections;

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverClass);
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(userName);
        dataSourceBuilder.password(password);
        HikariDataSource dataSource = (HikariDataSource) dataSourceBuilder.build();
        dataSource.setMinimumIdle(idleConnections);
        return dataSource;
    }
}
