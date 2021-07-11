package com.bencodez.aylachat.objects;

import lombok.Getter;
import lombok.Setter;

public class MessageData {
	@Getter
	@Setter
	private String player;
	@Getter
	@Setter
	private String message;
	@Getter
	@Setter
	private String channel;
	@Getter
	@Setter
	private String rawMessage;

	public MessageData(String player, String channel, String message, String rawMessage) {
		this.player = player;
		this.channel = channel;
		this.message = message;
		this.rawMessage = rawMessage;
	}

}
