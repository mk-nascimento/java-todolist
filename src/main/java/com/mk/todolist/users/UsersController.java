package com.mk.todolist.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private IUserRepository usersRepository;

    @PostMapping()
    public ResponseEntity create(@RequestBody UserModel model) {
        var dbUser = this.usersRepository.findByUsername(model.getUsername());

        if (dbUser instanceof UserModel) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }

        var pass = BCrypt.withDefaults().hashToString(12, model.getPassword().toCharArray());
        model.setPassword(pass);

        var user = this.usersRepository.save(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping()
    public ResponseEntity read() {
        var users = this.usersRepository.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
