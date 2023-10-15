package com.mk.todolist.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mk.todolist.exceptions.ErrorResponse;
import com.mk.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository tasksRepository;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody TaskModel entity, HttpServletRequest req) {
        var userId = req.getAttribute("id");
        entity.setUserId((UUID) userId);

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(entity.getStartAt()) || now.isAfter(entity.getEndAt())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid \"StartAt\" or \"EndAt\"."));
        } else if (entity.getStartAt().isAfter(entity.getEndAt())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("\"StartAt\" must be before \"EndAt\"."));
        }

        TaskModel task = this.tasksRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping()
    public List<TaskModel> read(HttpServletRequest req) {
        var userId = req.getAttribute("id");

        return this.tasksRepository.findByUserId((UUID) userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel entity, @PathVariable UUID id, HttpServletRequest req) {
        TaskModel instance = this.tasksRepository.findById(id).orElse(null);
        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(String.format("Task #%s not found", id)));
        }

        var userId = req.getAttribute("id");
        if (!instance.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Insufficient permission"));
        }

        Utils.copyNonNull(entity, instance);

        TaskModel task = this.tasksRepository.save(instance);
        return ResponseEntity.ok().body(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id, HttpServletRequest req) {
        System.out.println(id);
        TaskModel instance = this.tasksRepository.findById(id).orElse(null);
        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(String.format("Task #%s not found", id)));
        }

        var userId = req.getAttribute("id");
        if (!instance.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Insufficient permission"));
        }

        this.tasksRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
