package application;

import application.exceptions.UnableToCreateMUIAException;
import common.Configuration;
import common.Logger;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import receiving.ConnectionManager;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static MUIA self;
	private static int secondsRunning = 30;
	
	public static void main(String[] args) throws InterruptedException {
		try{
			ConnectionManager connectionManager = new ConnectionManager();
			Thread cmThread = new Thread(connectionManager);
			cmThread.start();
			//Thread.sleep(secondsRunning * 1000);
			//connectionManager.stop();
			//cmThread.join();
		}catch(Exception e){
			System.out.println("Error: "+ e.getMessage());
		}
		
		try {
			LocateRegistry.createRegistry(Integer.parseInt(Configuration.get(Configuration.REGISTRY_PORT)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			Main.self = new OriginalMUIA(Configuration.get(Configuration.MUIA_HOST_NAME),
					Configuration.get(Configuration.MUIA_HOST_IP),
					Integer.parseInt(Configuration.get(Configuration.MUIA_HOST_PORT)),
					Integer.parseInt(Configuration.get(Configuration.REGISTRY_PORT)));
			Logger.info("MUIA host initialized");
		} catch (UnknownHostException ex) {
			Logger.error("Unable to instantiate the host MUIA. Error: " 
					+ ex.getMessage());
		} catch (UnableToCreateMUIAException e) {
			// Finish the system...
		}
	}
	
	public static MUIA getSelf() {
		return self;
	}
}
