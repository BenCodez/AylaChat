package com.Ben12345rocks.AylaChat.Objects;

public class MessageData {
	private String player;
	private String message;
	private String channel;

	public MessageData(String player, String channel, String message) {
		this.player = player;
		this.channel = channel;
		this.message = message;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
