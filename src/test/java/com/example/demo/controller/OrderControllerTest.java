package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
	@Autowired
	OrderController orderController;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;

	void setUp() {
		orderRepository = mock(OrderRepository.class);
		userRepository = mock(UserRepository.class);
		orderController = new OrderController(userRepository, orderRepository);
		setUpOrder();
	}

	public void setUpOrder() {
		Item item = new Item();
		item.setId(1L);
		item.setName("Americano");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("Espresso with water");
		List<Item> items = new ArrayList<Item>();
		items.add(item);

		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		cart.setId(0L);
		cart.setUser(user);
		cart.setItems(items);
		BigDecimal total = BigDecimal.valueOf(2.99);
		cart.setTotal(total);
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(userRepository.findByUsername("user")).thenReturn(null);

	}

	@Test
	public void submitOrderOK() {
		setUp();
		ResponseEntity<UserOrder> response = orderController.submit("test");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		UserOrder order = response.getBody();
		assertNotNull(order);
		assertEquals(1, order.getItems().size());
	}

	@Test
	public void submitOrderUserNotFound() {
		setUp();
		ResponseEntity<UserOrder> response = orderController.submit("user");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void getOrderForUserOK() {
		setUp();
		ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("test");
		assertNotNull(ordersForUser);
		assertEquals(200, ordersForUser.getStatusCodeValue());
		List<UserOrder> orders = ordersForUser.getBody();
		assertNotNull(orders);
	}

	@Test
	public void getOrderForUserNotFound() {
		setUp();
		ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("user");
		assertNotNull(ordersForUser);
		assertEquals(404, ordersForUser.getStatusCodeValue());
	}
}
