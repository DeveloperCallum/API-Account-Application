package com.beamedcallum.account.application.startup;

import com.beamedcallum.database.SqlDatabase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHandler {
    private SqlDatabase instance;

    @Bean
    public SqlDatabase sqlDatabase(){
        if (instance == null){
            String ip = "10.0.0.28";
            String port = "3306";
            String dbName = "MicroServices";
            String user = "root";
            String password = "whoAsked";

            instance = new SqlDatabase(ip, port, dbName, user, password);
        }

        return instance;
    }
}
