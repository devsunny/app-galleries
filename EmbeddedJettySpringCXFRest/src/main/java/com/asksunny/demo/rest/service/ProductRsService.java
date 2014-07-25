package com.asksunny.demo.rest.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.asksunny.demo.rest.domain.Product;


@Path("/product")
public class ProductRsService {
	
	@Inject private ProductService  productService;
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})	
	public List<Product> listProduct(@QueryParam("page") @DefaultValue(value="1") int pagnum )
	{		
		return productService.getProducts(pagnum, 20);		
	}

}
