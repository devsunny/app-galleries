
package org.dcache.chimera;

import java.io.IOException;

import org.dcache.xdr.OncRpcSvc;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class HdfsNFSGatewayBootstrap {

    private HdfsNFSGatewayBootstrap() {
        // this class it used only to bootstrap the Spring IoC
    }

    public static void main(String[] args) throws IOException {       

        try {
           // ApplicationContext context = new FileSystemXmlApplicationContext(args[0]);
            
            ApplicationContext context = new ClassPathXmlApplicationContext("hdfs-nfs-gateway.xml");
            OncRpcSvc service = (OncRpcSvc) context.getBean("oncrpcsvc");
            service.start();
            System.in.read();
        } catch (BeansException e) {
            System.err.println("Spring: " + e.getMessage());
            System.exit(1);
        }
    }
}
