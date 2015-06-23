/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import packets.ChannelCreatingHeader;
import packets.ConnectionHeader;
import packets.MessageData;
import packets.MessagePacket;
import packets.MessagingHeader;
import packets.Packet;
import packets.RegistrationHeader;
import packets.SubscribeHeader;

/**
 *
 * @author cleber
 */
public class Teste {

	private static String json = "{\"status\" : 41,\"channel-key\" : \"HUEHUE\"}";

	public static void main(String[] args) {
		Gson gson = new Gson();
		String[] testes = new String[3];
		testes[0] = "cleber";
		testes[1] = "bruno";
		testes[2] = "douglas";
		JsonObject json = new JsonObject();
		JsonArray array = new JsonArray();
		for(String teste: testes){
			temp = new JsonObject();
			temp.addProperty("teste", teste);
			array.add(temp.get("teste"));
		}
		json.add("minha", array);
		System.out.println(gson.toJson(json));
		
		
	}
}
