package playground.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import playground.dto.Person;

@Repository
public interface IPersonRepository extends JpaRepository<Person, Long> {

    Person findById(long id);

    @Query(value = "SELECT * FROM Person " +
            "WHERE firstName = :firstName " +
            "AND lastName = :lastName " +
            "ORDER BY id DESCENDING",
            nativeQuery = true)
    Person queryByFullName(String firstName, String lastName);

}