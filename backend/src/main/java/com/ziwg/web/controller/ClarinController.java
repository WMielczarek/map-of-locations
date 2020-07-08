package com.ziwg.web.controller;


import com.ziwg.service.ClarinClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidParameterException;


@Log4j2
@RestController
@RequestMapping("/api")
public class ClarinController {

    public static final String DEFAULT_USER = "demo";

    private final ClarinClient clarinClient;

    @Autowired
    public ClarinController(ClarinClient clarinClient) {
        this.clarinClient = clarinClient;
    }

    @GetMapping("/startAnalyze")
    public ResponseEntity<Object> startAnalyze(@RequestParam String fileHandler,
                                               @RequestParam(defaultValue = DEFAULT_USER) String user,
                                               @RequestParam String projectName) {
        try {
            return ResponseEntity.ok(clarinClient.startAnalyze(fileHandler, user, projectName));
        } catch (InvalidParameterException e) {
            log.error("Project with this name exists", e);
            return ResponseEntity.badRequest().body("Project with this name exists");
        }
    }

    @GetMapping("/checkStatus/{processId}")
    public ResponseEntity<Object> getClarinStatus(@PathVariable String processId) {
        return ResponseEntity.ok(clarinClient.checkClarinStatus(processId));
    }

    @GetMapping("/result")
    public ResponseEntity<Object> fetchResult(@RequestParam String fileHandler,
                                              @RequestParam(defaultValue = DEFAULT_USER) String user,
                                              @RequestParam String projectName) {
        try {
            return ResponseEntity.ok(clarinClient.getClarinResult(fileHandler, user, projectName).orElseThrow(InvalidParameterException::new).getId());
        } catch (InvalidParameterException e) {
            log.error("Project with given name doesn't exist", e);
            return ResponseEntity.badRequest().body("Project with given name doesn't exist");
        } catch (IOException e) {
            log.error(e);
            return ResponseEntity.badRequest().body(e.getCause());
        }

    }
}
