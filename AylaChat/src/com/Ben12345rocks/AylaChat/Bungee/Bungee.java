package com.Ben12345rocks.AylaChat.Bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Bungee extends Plugin implements net.md_5.bungee.api.plugin.Listener {

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		this.getProxy().registerChannel("AylaChat");
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent ev) {

		if (!ev.getTag().equals("AylaChat")) {
			return;
		}
		// getProxy().getLogger().info("Got plugin message");
		ByteArrayInputStream instream = new ByteArrayInputStream(ev.getData());
		DataInputStream in = new DataInputStream(instream);
		try {
			String subchannel = in.readUTF();
			// System.out.println(subchannel);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			if (subchannel.equals("Chat")) {
				String chatchannel = in.readUTF();
				String message = in.readUTF();
				String playerName = in.readUTF();
				out.writeUTF("Chat");
				out.writeUTF(chatchannel);
				out.writeUTF(message);
				out.writeUTF(playerName);
				for (String send : getProxy().getServers().keySet()) {
					if (getProxy().getServers().get(send).getPlayers().size() > 0) {
						getProxy().getServers().get(send).sendData("AylaChat", outstream.toByteArray());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
