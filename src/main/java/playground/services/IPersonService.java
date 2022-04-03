package playground.services;

import playground.dto.Person;

public interface IPersonService {

    Person getPersonById(long id);

    Person getPersonByFullName(String firstName, String lastName);
  
}