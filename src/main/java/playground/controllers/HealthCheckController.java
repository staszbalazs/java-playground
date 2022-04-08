package playground.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import playground.aspects.Monitor;

@RestController
@RequestMapping(value = "/")
public class HealthCheckController {

    @GetMapping("/check")
    @Monitor(showArgs = true, showResult = true)
    public String check() {
        return "I am healthy!";
    }

}
