package com.example.demo.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

public class UserControllerTest {
	@Autowired
	UserController userController;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		cartRepository = mock(CartRepository.class);
		bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
		userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
		setupUser();
	}

	private void setupUser() {
		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(userRepository.findById(0L)).thenReturn(Optional.of(user));
		when(userRepository.findByUsername("user")).thenReturn(null);
	}

	@Test
	void testCreateUserValidRequest() {
		// Arrange
		when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashPassword");
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername("test");
		createUserRequest.setPassword("testPassword");
		createUserRequest.setConfirmPassword("testPassword");
		final ResponseEntity<User> response = userController.createUser(createUserRequest);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals("test", user.getUsername());
		assertEquals("hashPassword", user.getPassword());
	}

	@Test
	public void findUserByNameNotFound() {
		final ResponseEntity<User> response = userController.findByUserName("user");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void findUserByIdSuccess() {
		final ResponseEntity<User> response = userController.findById(0L);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
	}

	@Test
	public void findUserByNameSuccess() {
		final ResponseEntity<User> response = userController.findByUserName("test");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals("test", user.getUsername());
	}

	@Test
	public void findUserByIdNotFound() {
		final ResponseEntity<User> response = userController.findById(1L);
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
}
