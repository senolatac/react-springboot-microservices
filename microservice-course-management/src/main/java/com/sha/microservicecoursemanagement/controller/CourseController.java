package com.sha.microservicecoursemanagement.controller;

import com.sha.microservicecoursemanagement.intercomm.UserClient;
import com.sha.microservicecoursemanagement.model.Transaction;
import com.sha.microservicecoursemanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private Environment env;

    @Value("${spring.application.name}")
    private String serviceId;

    @GetMapping("/service/port")
    public String getPort(){
        return "Service is working at port : " + env.getProperty("local.server.port");
    }

    @GetMapping("/service/instances")
    public ResponseEntity<?> getInstances() {
        return ResponseEntity.ok(discoveryClient.getInstances(serviceId));
    }

    @GetMapping("/service/user/{userId}")
    public ResponseEntity<?> findTransactionsOfUser(@PathVariable Long userId){
        return ResponseEntity.ok(courseService.findTransactionsOfUser(userId));
    }

    @GetMapping("/service/all")
    public ResponseEntity<?> findAllCourses(){
        return ResponseEntity.ok(courseService.allCourses());
    }

    @PostMapping("/service/enroll")
    public ResponseEntity<?> saveTransaction(@RequestBody Transaction transaction) {
        transaction.setDateOfIssue(LocalDateTime.now());
        transaction.setCourse(courseService.findCourseById(transaction.getCourse().getId()));
        return new ResponseEntity<>(courseService.saveTransaction(transaction), HttpStatus.CREATED);
    }

    @GetMapping("/service/course/{courseId}")
    public ResponseEntity<?> findStudentsOfCourse(@PathVariable Long courseId){
        List<Transaction> transactions = courseService.findTransactionsOfCourse(courseId);
        if(CollectionUtils.isEmpty(transactions)){
           return ResponseEntity.notFound().build();
        }
        List<Long> userIdList = transactions.parallelStream().map(t -> t.getUserId()).collect(Collectors.toList());
        List<String> students = userClient.getUserNames(userIdList);
        return ResponseEntity.ok(students);
    }
}
