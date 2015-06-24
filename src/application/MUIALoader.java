package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import sending.Channel;
import application.exceptions.UnableToCreateMUIAException;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import common.Configuration;
import common.Logger;

public class MUIALoader implements Runnable{

	@Override
	public void run() {
		loadMuiaNetwork();
		loadDefaultChannels();
		Logger.debug("MUIA loader finished!");
	}
	
	@SuppressWarnings("unchecked")
	public void loadMuiaNetwork() {
		Gson gson = new Gson();
		Reader jsonFile = null;
		try {
			jsonFile = new FileReader(Configuration.get(Configuration.MUIA_NETWORK_FILE));
		} catch (FileNotFoundException e) {
			Logger.error("MUIA network file not found");
			return;
		}
		
		try {
			HashMap<String, Object> result = gson.fromJson(jsonFile, HashMap.class);
			ArrayList<LinkedTreeMap<String, Object>> knownMUIAs;
			knownMUIAs = (ArrayList<LinkedTreeMap<String, Object>>) result.get("knownMUIAs");
			
			String muiaName = null;
			String muiaIp = null;
			Integer muiaport = null;
			Integer muiaRegistryPort = null;
			CopyMUIA tmpMuiaCopy;
			for( LinkedTreeMap<String, Object> knownMUIA : knownMUIAs ) {
				try {
					muiaName = (String) knownMUIA.get("muia_name");
					muiaIp = (String) knownMUIA.get("muia_ip_address");
					muiaport = Integer.valueOf(((Double) knownMUIA.get("muia_server_port")).intValue());
					muiaRegistryPort = Integer.valueOf(((Double) knownMUIA.get("muia_registry_port")).intValue());
					
					try {
						tmpMuiaCopy = new CopyMUIA(muiaName, muiaIp, muiaport, muiaRegistryPort);
						Main.getSelf().addKnownMUIA(tmpMuiaCopy);
					} catch (UnableToCreateMUIAException e) {
						Logger.error("Cannot export MUIA copy reference: " + e.getMessage() );
					}
				} catch( Exception e ) {
					Logger.error("Could not load known MUIA: " + e.getMessage());
				}
			}
		} catch( Exception e ) {
			Logger.error("Could not load MUIA network file");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadDefaultChannels() {
		Gson gson = new Gson();
		Reader jsonFile = null;
		try {
			jsonFile = new FileReader(Configuration.get(Configuration.DEFAULT_CHANNELS_FILE));
		} catch (FileNotFoundException e) {
			Logger.error("MUIA default channels file not found");
			return;
		}
		
		try {
			HashMap<String, Object> result = gson.fromJson(jsonFile, HashMap.class);
			ArrayList<LinkedTreeMap<String, Object>> channels;
			channels = (ArrayList<LinkedTreeMap<String, Object>>) result.get("channels");
			
			String channelId;
			String channelDescription;
			Integer channelMaxSubscribers;
			Integer channelMaxRetries;
			Long channelRetryInterval;
			Long channelTimeout;
			Channel tmpChannel;
			for( LinkedTreeMap<String, Object> channel : channels ) {
				try {
					channelId = (String) channel.get("channel_id");
					channelDescription = (String) channel.get("channel_description");
					channelMaxSubscribers = Integer.valueOf(
							((Double) channel.get("channel_max_subscribers")).intValue());
					channelMaxRetries = Integer.valueOf(((Double) channel.get("channel_max_retries")).intValue());
					channelRetryInterval = Math.round((Double) channel.get("channel_retry_interval"));
					channelTimeout = Math.round((Double) channel.get("channel_send_timeout"));
					
					tmpChannel = new Channel(channelId, channelDescription, channelMaxSubscribers, channelMaxRetries,
							channelRetryInterval, channelTimeout);
					Main.getSelf().addChannel(tmpChannel);
				} catch( Exception e ) {
					Logger.error("Could not load default channel: " + e.getMessage());
				}
			}
		} catch( Exception e ) {
			Logger.error("Could not load default channels file");
		}
	}
}
