package com.howtodoinjava.app.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.howtodoinjava.app.model.User;

@RestController
public class AppController {

	private final String URI_USERS = "/users";
	private final String URI_USERS_ID = "/users/{id}";

	@Autowired
	RestTemplate restTemplate;

	// ************GET APIs**************//

	// 1. getForObject(url, classType)
	@GetMapping("all-users_v1")
	public ResponseEntity<List<User>> getAll_v1() {
		User[] usersArray = restTemplate.getForObject(URI_USERS, User[].class);
		return new ResponseEntity<>(Arrays.asList(usersArray), HttpStatus.OK);
	}

	@GetMapping("all-users_v1/{id}")
	public ResponseEntity<User> getById_v1(@PathVariable final long id) {
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");

		User user = restTemplate.getForObject(URI_USERS_ID, User.class, params);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	// 2. getForEntity(url, responseType)
	@GetMapping("all-users_v2")
	public ResponseEntity<User[]> getAll_v2() {
		ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(URI_USERS, User[].class);
		return responseEntity;
	}

	@GetMapping("all-users_v2/{id}")
	public ResponseEntity<User> getById_v2(@PathVariable final long id) {
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");

		ResponseEntity<User> responseEntity 
			= restTemplate.getForEntity(URI_USERS_ID, User.class, params);
		return responseEntity;
	}

	// 3. exchange(url, method, requestEntity, responseType)
	@GetMapping("all-users_v3")
	public ResponseEntity<User[]> getAll_v3() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("X-HEADER_NAME", "XYZ");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<User[]> responseEntity = restTemplate
				.exchange("/users", HttpMethod.GET, entity, User[].class);
		return responseEntity;
	}

	@GetMapping("all-users_v3/{id}")
	public ResponseEntity<User> getById_v3(@PathVariable final long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("X-HEADER_NAME", "XYZ");

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<User> responseEntity = restTemplate
				.exchange("/users/" + id, HttpMethod.GET, entity, User.class);
		return responseEntity;
	}

	// ************POST APIs**************//

	@PostMapping("users")
	public User create(@RequestBody final User newUser) {
		User createdUser = restTemplate.postForObject(URI_USERS, newUser, User.class);
		return createdUser;
	}

}
