package com.aman.logroutingservice.consumer.controller;

import com.aman.logroutingservice.model.migration.MigrationResponse;
import com.aman.logroutingservice.consumer.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class MigrationController {

    @Autowired
    private MigrationService migrationService;

    @PostMapping(value = "/migrate")
    public ResponseEntity<MigrationResponse> migrate() {
        MigrationResponse migrationResponse = migrationService.migrate();
        return ResponseEntity
                .created(URI.create(String.format("%s", migrationResponse.isStatus())))
                .body(migrationResponse);
    }


}
