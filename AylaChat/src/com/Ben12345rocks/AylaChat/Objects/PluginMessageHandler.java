package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

public abstract class PluginMessageHandler {

	public PluginMessageHandler() {
		
	}
	
	public abstract void onRecieve(String subChannel, ArrayList<String> messageData);

}
