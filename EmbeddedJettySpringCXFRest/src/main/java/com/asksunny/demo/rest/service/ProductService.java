package com.asksunny.demo.rest.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.asksunny.demo.rest.domain.Product;

@Service
public class ProductService {

	public List<Product> getProducts(int pageNumber, int pageSize) {
		List<Product> products = new ArrayList<Product>(pageSize);
		SecureRandom rand = new SecureRandom((new Date()).toString().getBytes());
		for (int index = 0; index < pageSize; ++index) {
			Product p = new Product();
			p.setName(String.format("Name-%d_%d", pageNumber, index));
			p.setDescription(String.format("Name-%d_%d", pageNumber, index));
			p.setManfacture(String.format("Name-%d_%d", pageNumber, index));
			p.setQuantity(index*pageSize);
			p.setMsrp(rand.nextDouble() * index * 10);
			products.add(p);
		}
		return products;
	}
	
	
	
}
