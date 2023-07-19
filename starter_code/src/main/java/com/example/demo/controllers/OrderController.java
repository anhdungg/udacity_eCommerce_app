package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {


	private UserRepository userRepository;

	private OrderRepository orderRepository;

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}


	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.debug("OrderController.submit: START");
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.info("OrderController.submit: Cant find user.");
			log.info("OrderController.submit: Order failed.");
			log.debug("OrderController.submit: END");
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("OrderController.submit: Order successfully.");
		log.debug("OrderController.submit: END");
		return ResponseEntity.ok(order);
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.debug("OrderController.getOrdersForUser: START");
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.info("OrderController.getOrdersForUser: Cant find user.");
			log.info("OrderController.getOrdersForUser: END");
			return ResponseEntity.notFound().build();
		}
		List<UserOrder> userOrderList = orderRepository.findByUser(user);
		log.info("OrderController.getOrdersForUser: Order of user: " + userOrderList.toString());
		log.debug("OrderController.getOrdersForUser: END");
		return ResponseEntity.ok(userOrderList);
	}
}
