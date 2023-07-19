package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
	@Autowired
	ItemController itemController;
	@Autowired
	ItemRepository itemRepository;

	@BeforeEach
	void setUp() {
		itemRepository = mock(ItemRepository.class);
		itemController = new ItemController(itemRepository);
		setupItem();
	}

	private void setupItem() {
		Item item = new Item();
		item.setId(1L);
		item.setName("Americano");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("Espresso with water");
		when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
		when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
		when(itemRepository.findByName("Americano")).thenReturn(Collections.singletonList(item));
	}

	@Test
	public void getAllItemOK() {
		ResponseEntity<List<Item>> response = itemController.getItems();
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		List<Item> items = response.getBody();
		assertNotNull(items);
		assertEquals(1, items.size());
	}

	@Test
	public void getItemByIDOK() {
		ResponseEntity<Item> response = itemController.getItemById(1L);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		Item item = response.getBody();
		assertNotNull(item);
	}

	@Test
	public void getItemByIDNotFound() {
		ResponseEntity<Item> response = itemController.getItemById(2L);
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void getItemByNameOK() {
		ResponseEntity<List<Item>> response = itemController.getItemsByName("Americano");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		List<Item> items = response.getBody();
		assertNotNull(items);
		assertEquals(1, items.size());
	}

	@Test
	public void getItemByNameNotFound() {
		ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
}
