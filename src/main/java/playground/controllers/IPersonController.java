package playground.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import playground.dto.Person;

@RequestMapping("/person")
public interface IPersonController {

    @GetMapping("/{id}")
    Person getPersonById(@PathVariable("id") long id);

}
