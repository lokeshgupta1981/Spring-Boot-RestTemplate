package com.howtodoinjava.app.model;

import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate dateOfBirth;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(final LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", " + "lastName=" + lastName + ", email=" + email
				+ ", dateOfBirth=" + dateOfBirth + "]";
	}
}
