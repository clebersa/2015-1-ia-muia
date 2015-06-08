package application;

import common.Configuration;
import common.Logger;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import receiving.ConnectionManager;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static MUIA self;
	private static Registry registry;
	private static int secondsRunning = 30;
	
	public static void main(String[] args) throws InterruptedException {
		try{
			ConnectionManager connectionManager = new ConnectionManager();
			Thread cmThread = new Thread(connectionManager);
			cmThread.start();
			Thread.sleep(secondsRunning * 1000);
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
		
		try {
			Main.self = new MUIA(Configuration.get(Configuration.MUIA_HOST_NAME),
					Configuration.get(Configuration.MUIA_HOST_IP),
					Integer.parseInt(Configuration.get(Configuration.MUIA_HOST_PORT)),
					false);
		} catch (UnknownHostException ex) {
			Logger.error("Unable to instantiate the host MUIA. Error: " 
					+ ex.getMessage());
		}
	}
	
	public static MUIA getSelf() {
		return self;
	}
	
	public static Registry getRegistry() {
		return registry;
	}
}
