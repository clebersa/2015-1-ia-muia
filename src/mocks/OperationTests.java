/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mocks;

import common.Configuration;
import common.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author cleber
 */
public class OperationTests {

	public static void main(String[] args) {
//		Logger.info("===== Connection Header Test BEGIN =====");
//		sendMessage(getMsgConnectionHeaderOK1());
//		Logger.info("===== Connection Header Test END =====");
//		Logger.info("===== Connection Header Test BEGIN =====");
//		sendMessage(getMsgConnectionHeaderER1());
//		Logger.info("===== Connection Header Test END =====");
//		Logger.info("===== Connection Header Test BEGIN =====");
//		sendMessage(getMsgConnectionHeaderER2());
//		Logger.info("===== Connection Header Test END =====");
//		Logger.info("===== Connection Header Test BEGIN =====");
//		sendMessage(getMsgConnectionHeaderER3());
//		Logger.info("===== Connection Header Test END =====");

//		Logger.info("===== Registration Header Test BEGIN =====");
//		sendMessage(getMsgRegistrationHeaderOK1());
//		Logger.info("===== Registration Header Test END =====");
//		Logger.info("===== Registration Header Test BEGIN =====");
//		sendMessage(getMsgRegistrationHeaderOK2());
//		Logger.info("===== Registration Header Test END =====");
//		Logger.info("===== Registration Header Test BEGIN =====");
//		sendMessage(getMsgRegistrationHeaderER1());
//		Logger.info("===== Registration Header Test END =====");
//		Logger.info("===== Message Data Test BEGIN =====");
//		sendMessage(getMsgMessageDataOK1());
//		Logger.info("===== Message Data Test END =====");
//		Logger.info("===== Message Data Test BEGIN =====");
//		sendMessage(getMsgMessageDataOK2());
//		Logger.info("===== Message Data Test END =====");
//		Logger.info("===== Message Data Test BEGIN =====");
//		sendMessage(getMsgMessageDataER1());
//		Logger.info("===== Message Data Test END =====");
		
//		Logger.info("===== Registration Message Test BEGIN =====");
//		sendMessage(getMsgMessagePacketOK1());
//		Logger.info("===== Registration Message Test END =====");
		
		Logger.info("===== Registration Message Test BEGIN =====");
		sendMessage(getMsgRegisterTrue());
		Logger.info("===== Registration Message Test END =====");
	}

	public static void sendMessage(String message) {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			Socket socket = new Socket(Configuration.get(
					Configuration.CONNECTION_SERVER_IP), Integer.parseInt(
							Configuration.get(Configuration.CONNECTION_SERVER_PORT)));
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			out.println(message);
			socket.shutdownOutput();

			Logger.debug("Waiting status...");
			String result = "", line;
			while ((line = in.readLine()) != null) {
				Logger.debug("Result Line: \"" + line + "\"");
				result += line;
			}

			Logger.info("Registration result: " + result);
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

	public static String getMsgConnectionHeaderOK1() {
		return "{\n"
				+ "	\"app-type\" : \"client\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0.1\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgConnectionHeaderER1() {
		return "{\n"
				+ "	\"app-type\" : \"unknown\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0.1\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgConnectionHeaderER2() {
		return "{\n"
				+ "	\"app-type\" : \"client\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0.300\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgConnectionHeaderER3() {
		return "{\n"
				+ "	\"app-type\" : \"client\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0\",\n"
				+ "	\"app-port\" : \"unk\"\n"
				+ "}";
	}

	public static String getMsgRegistrationHeaderOK1() {
		return "{\n"
				+ "	\"register\" : \"true\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgRegistrationHeaderOK2() {
		return "{\n"
				+ "	\"register\" : \"false\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgRegistrationHeaderER1() {
		return "{\n"
				+ "	\"register\" : \"unk\",\n"
				+ "	\"app-name\" : \"app1\",\n"
				+ "	\"app-address\" : \"10.0.0\",\n"
				+ "	\"app-port\" : \"1234\"\n"
				+ "}";
	}

	public static String getMsgMessageDataOK1() {
		return "{\n"
				+ "	\"value\" : \"" + DatatypeConverter.printBase64Binary(
						"Minha mensagem de teste".getBytes()) + "\""
				+ "}";
	}

	public static String getMsgMessageDataOK2() {
		return "{\n"
				+ "	\"value\" : \"\""
				+ "}";
	}

	public static String getMsgMessageDataER1() {
		return "{}";
	}

	public static String getMsgMessagePacketOK1() {
		return "{\n"
				+ "		\"header-type\" : \"registration\",\n"
				+ "		\"header-data\" : {\n"
				+ "			\"app-name\" : \"app1\",\n"
				+ "			\"app-address\" : \"10.0.0.1\",\n"
				+ "			\"app-port\" : \"1234\",\n"
				+ "			\"register\" : \"true\"\n"
				+ "		}\n"
				+ "	}";
	}

	public static String getMsgRegisterTrue() {
		return "{\n"
				+ "	\"connection-header\" : {\n"
				+ "		\"app-type\" : \"client\",\n"
				+ "		\"app-name\" : \"app1\",\n"
				+ "		\"app-address\" : \"10.0.0.1\",\n"
				+ "		\"app-port\" : \"1234\"\n"
				+ "	},\n"
				+ "	\"message-packet\" : {\n"
				+ "		\"header-type\" : \"registration\",\n"
				+ "		\"header-data\" : {\n"
				+ "			\"app-name\" : \"app1\",\n"
				+ "			\"app-address\" : \"10.0.0.1\",\n"
				+ "			\"app-port\" : \"1234\",\n"
				+ "			\"register\" : \"true\"\n"
				+ "		}\n"
				+ "	}\n"
				+ "}";
	}
}
