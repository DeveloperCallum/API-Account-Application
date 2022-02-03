package com.beamedcallum.account.application.database;

import com.beamedcallum.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class SqlDatabase extends Database {
    Logger logger = LoggerFactory.getLogger(SqlDatabase.class);

    public SqlDatabase() {
    }

    @Override
    protected Connection acquireConnection() throws SQLException {
        try {
            synchronized (this) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(getConnectionURL());
            }
        } catch (ClassNotFoundException e) {
            //TODO: Custom Exception
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getConnectionURL() {
        String ip = "localhost";
        String port = "3306";
        String dbName = "user_information";
        String user = "Remote";
        String password = "jqTvEftV3OpucmxAbMuW";

        return "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?characterEncoding=utf8&" + "user=" + user + "&password=" + password;
    }
}
