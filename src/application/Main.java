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
	private static OriginalMUIA self;
	
	public static void main(String[] args) throws InterruptedException {
		try{
			ConnectionManager connectionManager = new ConnectionManager();
			Thread cmThread = new Thread(connectionManager);
			cmThread.start();
		}catch(Exception e){
			System.out.println("Error: "+ e.getMessage());
		}
		
		try {
			System.setProperty("java.rmi.server.hostname", Configuration.get(Configuration.MUIA_HOST_IP));
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
		
		MUIALoader mnl = new MUIALoader();
		Thread mnlt = new Thread(mnl);
		mnlt.start();
	}
	
	public static OriginalMUIA getSelf() {
		return self;
	}
}