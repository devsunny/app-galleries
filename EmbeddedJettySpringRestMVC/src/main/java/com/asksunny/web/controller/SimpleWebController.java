package com.asksunny.web.controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/web")
public class SimpleWebController {

	public SimpleWebController() {
		
	}
	
	@RequestMapping({"/home"})
	public String apphome() {	    
	    try {
			InputStream in = getClass().getResourceAsStream("/META-INF/views/apphome.html");
			String text = IOUtils.toString(in);
			System.out.println(text);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return "apphome";
	}

}
