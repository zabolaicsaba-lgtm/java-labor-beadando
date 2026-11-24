package org.example.javalaborbeadando.torony;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RestOldalController {

    @GetMapping("/rest")
    public String rest() {
        return "rest";
    }
}
