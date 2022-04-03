package playground.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import playground.repositories.IPersonRepository;
import playground.services.IPersonService;
import playground.services.PersonService;

@Configuration
public class PersonConfiguration {

    @Bean
    public IPersonService personService(IPersonRepository personRepository) {
        return new PersonService(personRepository);
    }

}
