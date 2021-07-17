package com.howtodoinjava.app.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.howtodoinjava.app.exception.RecordNotFoundException;
import com.howtodoinjava.app.exception.ServerException;
import com.howtodoinjava.app.model.User;
import com.howtodoinjava.app.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("users")
	public ResponseEntity<List<User>> getAll() {
		return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
	}

	@GetMapping("users/{id}")
	public ResponseEntity<User> getById(@PathVariable final long id) {
		Optional<User> user = userService.getById(id);
		if (user.isPresent()) {
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}
		else {
			throw new RecordNotFoundException();
		}
	}

	@PostMapping(path = "users",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody final User newUser) {
		User user = userService.save(newUser);
		if (user == null) {
			throw new ServerException();
		}
		else {
			return new ResponseEntity<>(user, HttpStatus.CREATED);
		}
	}

	@PutMapping("users/{id}")
	public ResponseEntity<User> update(@RequestBody final User updatedUser) {
		User user = userService.save(updatedUser);
		if (user == null) {
			throw new ServerException();
		}
		else {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}

	@DeleteMapping("users/{id}")
	public HttpStatus delete(@PathVariable final long id) {
		try {
			userService.delete(id);
			return HttpStatus.OK;
		}
		catch (Exception e) {
			throw new RecordNotFoundException();
		}
	}
}
