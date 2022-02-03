package com.beamedcallum.account.application.controller;

import com.beamedcallum.account.application.controller.enttiy.SystemMessage;
import com.beamedcallum.account.application.database.SqlDatabase;
import com.beamedcallum.database.accounts.ReadAccountQuery;
import com.beamedcallum.database.accounts.SaveAccountCommand;
import com.beamedcallum.database.accounts.models.Account;
import com.beamedcallum.database.accounts.models.AccountInfo;
import common.exception.RestRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

@RestController
public class BasicUserController {
    @Autowired
    private SqlDatabase sqlDatabase;

    @GetMapping("/users/{username}")
    public AccountInfo getAccount(@PathVariable String username) {
        ReadAccountQuery readUserQuery = new ReadAccountQuery(sqlDatabase, username);
        try {
            Optional<Account> accountOptional = readUserQuery.executeQuery();

            if (accountOptional.isEmpty()) {
                throw new RuntimeException("Account does not exist!");
            }

            Account account = accountOptional.get();
            return new AccountInfo(account);
        } catch (SQLException e) {
            //TODO: Custom Exception
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/users/{username}/password")
    public ResponseEntity<?> checkPassword(@PathVariable String username, @RequestBody String password) {
        ReadAccountQuery query = new ReadAccountQuery(sqlDatabase, username);

        try {
            Optional<Account> optionalAccount = query.executeQuery();
            Account account = optionalAccount.orElseThrow(() -> new RestRuntimeException("Data was invalid", HttpStatus.OK));

            if (!password.equals(account.getPassword())){
                throw new RestRuntimeException("Data was invalid", HttpStatus.OK);
            }

            return new ResponseEntity<>(new SystemMessage("Password Matched"), HttpStatus.OK);

        } catch (SQLException e) {
            throw new RestRuntimeException("Data was invalid", HttpStatus.OK);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        SystemMessage systemMessage = new SystemMessage("Successfully created new account!");

        SaveAccountCommand saveAccountCommand = new SaveAccountCommand(sqlDatabase, account);
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
}

