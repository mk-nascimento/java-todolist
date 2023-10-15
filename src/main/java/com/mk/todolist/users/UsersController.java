package com.mk.todolist.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mk.todolist.exceptions.ErrorResponse;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private IUserRepository usersRepository;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody UserModel entity) {
        var dbUser = this.usersRepository.findByUsername(entity.getUsername());

        if (dbUser instanceof UserModel) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User already exists"));
        }

        var pass = BCrypt.withDefaults().hashToString(12, entity.getPassword().toCharArray());
        entity.setPassword(pass);
        String username = entity.getUsername();
        entity.setUsername(username.trim());

        var user = this.usersRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
