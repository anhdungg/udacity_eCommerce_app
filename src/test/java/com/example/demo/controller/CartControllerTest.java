package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
	@Autowired
	CartController cartController;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	ItemRepository itemRepository;

	void setUp() {
		userRepository = mock(UserRepository.class);
		cartRepository = mock(CartRepository.class);
		itemRepository = mock(ItemRepository.class);
		cartController = new CartController(userRepository, cartRepository, itemRepository);
		setupCart();
	}

	private void setupCart() {
		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);

		Item item = new Item();
		item.setId(1L);
		item.setName("Americano");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("Espresso with water");
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

	}

	@Test
	public void addToCartOK() {
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
	}

	@Test
	public void addToCartWrongUser() {
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("username");
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void addToCartNotFoundItem() {
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(2L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void removeFromCartOK() {
		setUp();
		// Set up test by adding two items to cart.
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(2);
		modifyCartRequest.setUsername("test");
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");
		response = cartController.removeFromCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

	}

	@Test
	public void removeFromCartInvalidUser() {
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("username");
		ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void removeFromCartInvalidItem() {
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(2L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");
		ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

}
