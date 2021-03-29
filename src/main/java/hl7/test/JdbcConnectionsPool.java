package hl7.test;

import org.springframework.beans.BeansException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnectionsPool   {
//    private static C3P0ConnectionProvider c3P0ConnectionProvider = new C3P0ConnectionProvider();
    private static DriverManagerDataSource dataSource = new DriverManagerDataSource();
    static{
        dataSource.setUsername("root");
        dataSource.setPassword("bicon@123");
        dataSource.setUrl("jdbc:mysql://bd208:3306/test_hl7");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        Properties props = new Properties();
//        props.setProperty("user", "root");
//        props.setProperty("password", "root");
//        props.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/test");
//        props.setProperty("driverClass", "com.mysql.jdbc.Driver");
//        props.setProperty("initialPoolSize", "3");
//        props.setProperty("maxIdleTime", "3");
//        props.setProperty("maxPoolSize", "10");
//        props.setProperty("minPoolSize", "1");
//        c3P0ConnectionProvider.configure(props);
    }
    public Connection getConnection(){
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public DataSource getDateSource(){
        return dataSource;
    }
}
