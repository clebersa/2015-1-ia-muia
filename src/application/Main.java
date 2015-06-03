package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import receiving.ConnectionManager;

import sending.Channel;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static MUIA self;
	private static Registry registry;
	
	public static void main(String[] args) throws InterruptedException {
		try{
			ConnectionManager connectionManager = new ConnectionManager();
			Thread cmThread = new Thread(connectionManager);
			cmThread.start();
			Thread.sleep(5000);
			connectionManager.stop();
			
			cmThread.join();
			return;
		}catch(Exception e){
			System.out.println("Error: "+ e.getMessage());
		}
		try {
			Main.registry = LocateRegistry.createRegistry( 2001 );
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Main.self = new MUIA( false );
	}
	
	public static MUIA getSelf() {
		return Main.self;
	}
	
	public static Registry getRegistry() {
		return Main.registry;
	}
}
