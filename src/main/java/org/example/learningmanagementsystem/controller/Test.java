package org.example.learningmanagementsystem.controller;


import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class Test {

    // Oddiy matn qaytaruvchi GET so'rovi
    @GetMapping("/hello")
    public String sayHello() {
        return "Salom! Java Controller muvaffaqiyatli ishlayapti.";
    }

    // JSON formatida ma'lumot qaytaruvchi GET so'rovi
    @GetMapping("/users")
    public List<String> getUsers() {
        return Arrays.asList("Ali", "Vali", "G'ani");
    }

    // Parametr qabul qiluvchi GET so'rovi (masalan: /api/test/greet?name=Anvar)
    @GetMapping("/greet")
    public String greetUser(@RequestParam(defaultValue = "Mehmon") String name) {
        return "Xush kelibsiz, " + name + "!";
    }
}
