package com.bitbytejoy.neuefischetodo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bitbytejoy.neuefischetodo.configuration.Props;
import com.bitbytejoy.neuefischetodo.model.Login;
import com.bitbytejoy.neuefischetodo.model.LoginResponse;
import com.bitbytejoy.neuefischetodo.model.User;
import com.bitbytejoy.neuefischetodo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final Props props;

    public UserController(
        BCryptPasswordEncoder bCryptPasswordEncoder,
        UserRepository userRepository,
        Props props
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.props = props;
    }

    // Registration endpoint
    @PostMapping("/users")
    public @ResponseBody User post (@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @PostMapping("/login")
    public @ResponseBody LoginResponse login (@Valid @RequestBody Login login) {
        User user = userRepository.findByEmail(login.getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!bCryptPasswordEncoder.matches(
            login.getPassword(),
            user.getPassword()
        )) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setJwt(
            JWT
                .create()
                .withPayload(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail()
                )).sign(Algorithm.HMAC256(props.getJwtSecret()))
        );

        return loginResponse;
    }
}
