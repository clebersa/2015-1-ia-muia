package application;

import application.exceptions.UnableToCreateMUIAException;
import common.Configuration;
import common.Logger;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import receiving.ConnectionManager;
import sending.Channel;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static OriginalMUIA self;
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
			LocateRegistry.createRegistry(9990);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			Main.self = new OriginalMUIA(Configuration.get(Configuration.MUIA_HOST_NAME),
					Configuration.get(Configuration.MUIA_HOST_IP),
					Integer.parseInt(Configuration.get(Configuration.MUIA_HOST_PORT)),
					Integer.parseInt(Configuration.get(Configuration.REGISTRY_PORT)));
			Channel c1 = new Channel("CANAL 1");
			Main.self.addChannel(c1);
			Logger.info("MUIA host initialized");
			
			OriginalMUIA o = new OriginalMUIA( "muia2", "127.0.0.1", 2002, 9990 );
			CopyMUIA t1 = new CopyMUIA(Configuration.get(Configuration.MUIA_HOST_NAME),
					Configuration.get(Configuration.MUIA_HOST_IP),
					Integer.parseInt(Configuration.get(Configuration.MUIA_HOST_PORT)),
					Integer.parseInt(Configuration.get(Configuration.REGISTRY_PORT)));
			o.addKnownMUIA(t1);
			
			CopyMUIA t2 = new CopyMUIA("muia2", "127.0.0.1", 2002, 9990);
			
			Main.self.addKnownMUIA(t2);
			
			Thread.sleep(10000);
			Client c = new Client("app", "127.0.0.1", 2000);
			Channel c2 = new Channel("CANAL 2");
			
			o.addClient(c);
			o.addChannel(c2);
		} catch (UnknownHostException ex) {
			Logger.error("Unable to instantiate the host MUIA. Error: " 
					+ ex.getMessage());
		} catch (UnableToCreateMUIAException e) {
			// Finish the system...
		}
	}
	
	public static OriginalMUIA getSelf() {
		return self;
	}
}
