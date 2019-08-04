package com.sha.microservicecoursemanagement.repository;

import com.sha.microservicecoursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
