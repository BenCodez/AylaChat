package com.bencodez.aylachat.objects;

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

	public String getMessage() {
		return message;
	}

	public String getPlayer() {
		return player;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

}
