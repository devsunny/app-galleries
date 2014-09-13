package com.asksunny.appgallaries.cdp;

public class LoggingPipe implements Pipe {

	
	private String pipeName = null;
	
	public LoggingPipe() {		
	}

	public void init() {		

	}

	public void process(String[] data) 
	{		
		StringBuilder buf  = new StringBuilder();
		buf.append(getPipeName());
		buf.append(">>");
		for (int i = 0; i < data.length; i++) {			
			buf.append(data[i]);
			if(i<data.length-1){
				buf.append("<>");
			}
		}
		System.out.println(buf.toString());		
	}

	public void endOfDataStream() {		
		System.out.print(getPipeName() + " Done");		
	}

	public String getPipeName() {
		return pipeName;
	}

	public void setPipeName(String pipeName) {
		this.pipeName = pipeName;		
	}
	
	

}
