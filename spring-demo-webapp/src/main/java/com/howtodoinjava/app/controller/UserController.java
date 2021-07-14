package com.howtodoinjava.app.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.howtodoinjava.app.model.User;
import com.howtodoinjava.app.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("users")
	public List<User> getAll() {
		return userService.getAll();
	}

	@GetMapping("users/{id}")
	public Optional<User> getById(@PathVariable long id) {
		return userService.getById(id);
	}

	@PostMapping("users")
	public User createUser(@RequestBody User newUser) {
		return userService.save(newUser);
	}

	@PutMapping("users/{id}")
	public User update(@RequestBody User updatedUser) {
		return userService.save(updatedUser);
	}

	@DeleteMapping("users/{id}")
	public void update(@PathVariable long id) {
		userService.delete(id);
	}
}
