package playground.controllers;

import org.springframework.web.bind.annotation.RestController;
import playground.dto.Person;
import playground.services.IPersonService;

@RestController
public class PersonController implements IPersonController {

    private final IPersonService personService;

    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person getPersonById( long id) {
        return personService.getPersonById(id);
    }
}
