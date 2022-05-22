package com.beamedcallum.account.application.controller;

import com.beamedcallum.account.application.controller.enttiy.SystemMessage;
import com.beamedcallum.database.SqlDatabase;
import com.beamedcallum.database.accounts.actions.commands.CreateAccountCommand;
import com.beamedcallum.database.accounts.actions.queries.ReadAccountQuery;
import com.beamedcallum.database.accounts.models.Account;
import com.beamedcallum.database.accounts.models.AccountInfo;
import common.account.AccountBasicInfo;
import common.exception.RestRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

@RestController
public class BasicUserController {
    @Autowired
    private SqlDatabase sqlDatabase;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @GetMapping("/users/{user}")
    public ResponseEntity<?> getAccount(@PathVariable String user) throws SQLException {
        ReadAccountQuery query = new ReadAccountQuery(sqlDatabase, user, false);

        Optional<Account> optionalAccount = query.executeQuery();
        Account account = optionalAccount.orElseThrow(() -> {throw new RestRuntimeException("Account does not exist", HttpStatus.BAD_REQUEST);});

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/user/validate")
    public ResponseEntity<?> checkPassword(@RequestBody AccountBasicInfo accountInfo) {
        System.out.println("Finding Account: " + accountInfo.getUsername());
        ReadAccountQuery query = new ReadAccountQuery(sqlDatabase, accountInfo.getUsername(), true);

        try {
            Optional<Account> optionalAccount = query.executeQuery();
            Account account = optionalAccount.orElseThrow(() -> new RestRuntimeException("Username or password is incorrect.", HttpStatus.FORBIDDEN));

            if (!passwordEncoder.matches(accountInfo.getPassword(), account.getPassword())) {
                throw new RestRuntimeException("Username or password is incorrect.", HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(new SystemMessage("Password Matched"), HttpStatus.OK);

        }catch (SQLException e){
            e.printStackTrace();
            throw new RestRuntimeException("Unexpected Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<?> createAccount(@RequestBody AccountBasicInfo accountRaw) {
        Account.AccountBuilder accountBuilder = new Account.AccountBuilder();
        accountBuilder.setUsername(accountRaw.getUsername());
        accountBuilder.setPassword(accountRaw.getPassword());

        Account account = accountBuilder.build();

        SystemMessage systemMessage = new SystemMessage("Successfully created new account!");

        CreateAccountCommand saveAccountCommand = new CreateAccountCommand(sqlDatabase, passwordEncoder, account);

        try {
            saveAccountCommand.executeCommand();
        } catch (SQLIntegrityConstraintViolationException e) {
            //TODO: 409 - Create custom exception!
            throw new RestRuntimeException("Account already exists", HttpStatus.CONFLICT);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(systemMessage, HttpStatus.ACCEPTED);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

