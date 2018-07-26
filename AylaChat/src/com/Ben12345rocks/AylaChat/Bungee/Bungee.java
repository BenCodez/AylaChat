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
		this.getProxy().registerChannel("AylaChat:AylaChat");
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent ev) {
		if (!ev.getTag().equals("AylaChat")) {
			return;
		}
		ByteArrayInputStream instream = new ByteArrayInputStream(ev.getData());
		DataInputStream in = new DataInputStream(instream);
		try {
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			String subchannel = in.readUTF();
			int size = in.readInt();
			out.writeUTF(subchannel);
			out.writeInt(size);
			for (int i = 0; i < size; i++) {
				out.writeUTF(in.readUTF());
			}
			for (String send : getProxy().getServers().keySet()) {
				if (getProxy().getServers().get(send).getPlayers().size() > 0) {
					getProxy().getServers().get(send).sendData("AylaChat:AylaChat", outstream.toByteArray());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
