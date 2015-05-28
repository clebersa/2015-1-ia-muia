package main;

import java.util.ArrayList;

import classes.Channel;
import classes.KnownMUIA;

/**
 * Main class of MUIA.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Main {
	private static KnownMUIA self;
	private static ArrayList<Channel> activeChannels;
	
	public static void main(String[] args) {
		Main.self = new KnownMUIA();
		Main.activeChannels = new ArrayList<Channel>();
	}

	public static KnownMUIA getSelf() {
		return Main.self;
	}
	
	public static ArrayList<Channel> getActiveChannels() {
		return Main.activeChannels;
	}
}
