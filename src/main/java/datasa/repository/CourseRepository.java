package datasa.repository;

import datasa.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository
        extends JpaRepository<Course, Long> {

    static List<Course> findTop5ByOrderByViewCountDesc() {
        return null;
    }
}
