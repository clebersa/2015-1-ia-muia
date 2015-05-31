/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package receiving;

import common.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author cleber
 */
public class ConnectionHandler implements Runnable, ConnectionHandlerObservable {

	int id;

	private final Socket connection;

	ConnectionManager connectionManager;

	public ConnectionHandler(int id, Socket connection) {
		this.id = id;
		this.connection = connection;
	}

	@Override
	public void run() {
		String socketData = "";
		PrintWriter out = null;
		BufferedReader in = null;

		int result = 1;

		try {
			out = new PrintWriter(connection.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			Logger.debug("Receiving data in the ConnectionHandler " + id
					+ "...");

			String line;
			while ((line = in.readLine()) != null) {
				Logger.debug("Line: \"" + line + "\"");
				socketData += line;
				if("".equals(line)) break;
			}
			
			if ("".equals(socketData)) {
				Logger.debug("Stop signal received!");
				result = 0;
			} else {
				//Build packet...
			}
			
			out.println(result);
		} catch (Exception ex) {
			Logger.error("Unable to receive the socket data! Error: "
					+ ex.getMessage());
			result = 10;
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
		Logger.info("ConnectionHandler " + id + " result: " + result);
		notifyObservers();
	}

	public int getId() {
		return id;
	}

	@Override
	public void notifyObservers() {
		connectionManager.update(this);
	}

	@Override
	public boolean addObserver(ConnectionManager conMan) {
		if (connectionManager == null) {
			connectionManager = conMan;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeObserver(ConnectionManager conMan) {
		if (connectionManager == conMan) {
			connectionManager = null;
			return true;
		} else {
			return false;
		}
	}
}
