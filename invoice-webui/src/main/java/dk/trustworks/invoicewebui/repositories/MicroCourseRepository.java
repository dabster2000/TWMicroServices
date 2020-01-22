package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Bubble;
import dk.trustworks.invoicewebui.model.MicroCourse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "micro_courses", path="micro_courses")
public interface MicroCourseRepository extends CrudRepository<MicroCourse, Integer> {

    List<MicroCourse> findByActiveTrue();
    List<MicroCourse> findByActiveTrueOrderByCreatedDesc();

}
