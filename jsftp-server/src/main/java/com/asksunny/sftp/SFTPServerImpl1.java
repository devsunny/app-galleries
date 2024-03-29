package com.asksunny.sftp;

import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;

public class SFTPServerImpl1 {

	public static void main(String[] args) throws Exception {
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22999);
		
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			
			public boolean authenticate(String username, String password,
					ServerSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		
		CommandFactory myCommandFactory = new CommandFactory() {
			
			public Command createCommand(String command) {
				System.out.println("Command: " + command);
				return null;
			}
		};
		sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));
		
		
 
	    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
	    // this needs to be stubbed out based on your implementation
//	    namedFactoryList.add( new NamedFactory<Command>(){
//
//			public Command create() {
//				SftpSubsystem sftp = new SftpSubsystem();
//				
//				return sftp;
//			}
//
//			public String getName() {
//				return "SFTP stuff";
//			}
//	    	
//	    });
	    
	    namedFactoryList.add(new SftpSubsystem.Factory());
	    sshd.setSubsystemFactories(namedFactoryList);
		
	    
	    
	    
		sshd.start();
		
		System.out.println("Press enter to quit");
		System.in.read();
		sshd.stop();
	}

}
