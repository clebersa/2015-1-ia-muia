/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package receiving;

import common.Configuration;
import common.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Gerenciador de conexões responsável pore receber conexões de aplicações
 * clientes repassar cada conexão para uma instância de
 * {@link ConnectionHandler}.
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ConnectionManager implements Runnable, ConnectionHandlerObserver {

	private final ServerSocket serverSocket;
	private boolean keepRunning;
	private HashMap<Integer, ConnectionHandler> activeConnections;

	public ConnectionManager() throws Exception {
		try {
			String port = Configuration.get(Configuration.CONNECTION_SERVER_PORT);
			if (port == null) {
				throw new IllegalArgumentException("Port not defined.");
			}
			serverSocket = new ServerSocket(Integer.parseInt(port));

			activeConnections = new HashMap<>();
		} catch (IllegalArgumentException | IOException ex) {
			Logger.error("Unable to instantiate ConnectionManager. Error: "
					+ ex.getMessage());
			throw ex;
		}
	}

	public void stop() {
		keepRunning = false;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			Socket socket = new Socket(serverSocket.getInetAddress(), Integer.parseInt(
					Configuration.get(Configuration.CONNECTION_SERVER_PORT)));
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			Logger.info("Sending stop signal to the last connection of the "
					+ "ConnectionManager...");
			out.println();
			socket.shutdownOutput();
			
			Logger.debug("Waiting status...");
			String result = "", line;
			while ((line = in.readLine()) != null) {
				Logger.debug("Result Line: \"" + line + "\"");
				result += line;
			}
			
			
			Logger.info("Stop signal result of the last ConnectionHandler: " 
					+ result);
		} catch (Exception ex) {
			Logger.error("Unable to stop ConnectionManager! Error: "
					+ ex.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.error("Unable to close the BufferedReader. Error: "
							+ ex.getMessage());
				}
			}
		}
	}

	@Override
	public void run() {
		int threadNumber = 0;
		keepRunning = true;
		Logger.info("ConnectionManager started!");
		while (keepRunning) {
			try {
				activeConnections.put(threadNumber, new ConnectionHandler(
						threadNumber, serverSocket.accept()));
				activeConnections.get(threadNumber).addObserver(this);
				new Thread(activeConnections.get(threadNumber)).start();
				threadNumber++;
			} catch (Exception ex) {
				Logger.error("Unable to receive connection!" + ex.getMessage());
			}
		}
		Logger.info("Waiting while the active connections are finished...");
		while (!activeConnections.isEmpty()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Logger.warning("Unable to sleep. Error: " + ex.getMessage());
			}
		}
		Logger.info("ConnectionManager stopped!");
	}

	@Override
	public synchronized void update(ConnectionHandler conHandler) {
		if (activeConnections.remove(conHandler.getId()) != null) {
			Logger.debug("ConnectionHandler " + conHandler.getId() + " removed from active connections list.");
		} else {
			Logger.warning("ConnectionHandler " + conHandler.getId() + " not found for removal.");
		}
	}
}
