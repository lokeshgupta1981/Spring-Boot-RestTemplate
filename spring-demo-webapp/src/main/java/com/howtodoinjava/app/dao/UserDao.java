package com.howtodoinjava.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.app.model.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

}
