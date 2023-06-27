package com.example.zaliczenie_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BmiController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BmiRepository bmiRepository;

    @PostMapping("/bmi")
    public Bmi addBmi(@AuthenticationPrincipal Users user, @RequestBody Bmi newBmi) {

        if (user == null) {
            throw new IllegalArgumentException("User is not logged in or does not exist");
        }
        System.out.println("User id: " + user.getId());
        newBmi.setUser(user);
        newBmi.setBmi(newBmi.getWeight() / Math.pow(newBmi.getHeight() / 100.0, 2));
        return bmiRepository.save(newBmi);
    }

    @GetMapping("/bmi")
    public List<Bmi> getBmiByUser(@AuthenticationPrincipal Users user) {
        return bmiRepository.findByUser(user);
    }

}
