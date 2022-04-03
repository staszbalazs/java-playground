package playground.services;

import playground.dto.Person;
import playground.repositories.IPersonRepository;

public class PersonService implements IPersonService {

    private final IPersonRepository userRepository;

    public PersonService(IPersonRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Person getPersonById(long id) {
        return userRepository.findById(id);
    }

    public Person getPersonByFullName(String firstName, String lastName) {
        return userRepository.queryByFullName(firstName, lastName);
    }


}