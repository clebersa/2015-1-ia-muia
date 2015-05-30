package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import classes.Channel;
import classes.Client;
import classes.MUIA;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static MUIA self;
	private static Registry registry;
	
	public static void main(String[] args) throws InterruptedException {
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
