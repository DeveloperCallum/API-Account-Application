package com.beamedcallum.account.application.startup;

import com.beamedcallum.database.SqlDatabase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseStartup implements ApplicationRunner {
    public static SqlDatabase instance;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String ip = "localhost";
        String port = "3306";
        String dbName = "user_information";
        String user = "Remote";
        String password = "jqTvEftV3OpucmxAbMuW";

        instance = new SqlDatabase(ip, port, dbName, user, password);
    }

    @Bean
    public SqlDatabase sqlDatabase(){
        return instance;
    }
}
