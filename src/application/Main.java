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
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 * @since 28/05/2015
 */
public class Main {
	private static OriginalMUIA self;
	
	public static void main(String[] args) throws InterruptedException {
		String muiaName = Configuration.get(Configuration.MUIA_HOST_NAME);
		String muiaIP = Configuration.get(Configuration.MUIA_HOST_IP);
		Integer muiaServerPort = Integer.parseInt(Configuration.get(Configuration.MUIA_HOST_PORT));
		Integer muiaRegistryPort = Integer.parseInt(Configuration.get(Configuration.REGISTRY_PORT));
		
		try{
			ConnectionManager connectionManager = new ConnectionManager();
			Thread cmThread = new Thread(connectionManager);
			cmThread.start();
		}catch(Exception e){
			Logger.error("Unable to instantiate the Connection Manager of the MUIA");
		}
		
		try {
			System.setProperty("java.rmi.server.hostname", muiaIP);
			LocateRegistry.createRegistry(muiaRegistryPort);
		} catch (RemoteException e) {
			Logger.error("Unable to create registry: " + muiaIP + ":" + muiaRegistryPort);
			// Finish the system...
		}
		
		try {
			Main.self = new OriginalMUIA(muiaName, muiaIP, muiaServerPort, muiaRegistryPort);
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