package com.asksunny.nfs.hadoop;

import org.dcache.xdr.OncRpcSvc;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HadoopNfsGateway {

	public HadoopNfsGateway() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		try {
			// ApplicationContext context = new
			// FileSystemXmlApplicationContext(args[0]);
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"hdfs-nfs.xml");
			OncRpcSvc service = (OncRpcSvc) context.getBean("oncrpcsvc");
			service.start();
			System.in.read();
		} catch (BeansException e) {
			System.err.println("Spring: " + e.getMessage());
			System.exit(1);
		}
	}

}
