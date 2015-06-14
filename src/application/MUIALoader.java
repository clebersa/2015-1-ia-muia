package application;

import java.io.FileReader;
import java.io.IOException;

import sending.Channel;
import application.exceptions.UnableToCreateMUIAException;

import com.google.gson.stream.JsonReader;

import common.Configuration;
import common.Logger;

public class MUIALoader implements Runnable{

	@Override
	public void run() {
		loadMuiaNetwork();
		loadDefaultChannels();
	}
	
	public void loadMuiaNetwork() {
		try {
			JsonReader jsonReader = new JsonReader(
					new FileReader(Configuration.get(Configuration.MUIA_NETWORK_FILE)));
			try {
				jsonReader.beginArray();
				while( jsonReader.hasNext() ) {
					jsonReader.beginObject();
					String muiaName = null;
					String muiaIp = null;
					Integer muiaport = null;
					Integer muiaRegistryPort = null;
					
					while (jsonReader.hasNext()) {
						String name = jsonReader.nextName();
						if (name.equals("muia_name")) {
							muiaName = jsonReader.nextString();
						} else if (name.equals("muia_ip_address")) {
							muiaIp = jsonReader.nextString();
						} else if (name.equals("muia_server_port")) {
							muiaport = jsonReader.nextInt();
						} else if (name.equals("muia_registry_port")) {
							muiaRegistryPort = jsonReader.nextInt();
						} else {
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
					
					CopyMUIA muiaCopy;
					try {
						muiaCopy = new CopyMUIA(muiaName, muiaIp, muiaport, muiaRegistryPort);
						Main.getSelf().addKnownMUIA(muiaCopy);
					} catch (UnableToCreateMUIAException e) {
						Logger.error("Cannot export muia copy reference: " + e.getMessage() );
					}
				}
				jsonReader.endArray();
			} finally {
				jsonReader.close();
			}
		} catch (IOException e) {
			Logger.error("Cannot load muia network file. Error: " + e.getMessage() );
		}
	}
	
	public void loadDefaultChannels() {
		try {
			JsonReader jsonReader = new JsonReader(
					new FileReader(Configuration.get(Configuration.DEFAULT_CHANNELS_FILE)));
			try {
				jsonReader.beginArray();
				Channel channel;
				while( jsonReader.hasNext() ) {
					jsonReader.beginObject();
					String channelId = null;
					
					while (jsonReader.hasNext()) {
						String name = jsonReader.nextName();
						if (name.equals("channel_id")) {
							channelId = jsonReader.nextString();
						} else {
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
					
					channel = new Channel(channelId);
					Main.getSelf().addChannel(channel);
				}
				jsonReader.endArray();
			} finally {
				jsonReader.close();
			}
		} catch (IOException e) {
			Logger.error("Cannot load default channels file. Error: " + e.getMessage() );
		}
	}
}
