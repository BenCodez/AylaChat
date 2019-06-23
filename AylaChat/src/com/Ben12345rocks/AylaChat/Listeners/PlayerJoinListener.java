package com.Ben12345rocks.AylaChat.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Ben12345rocks.AdvancedCore.Listeners.AdvancedCoreLoginEvent;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class PlayerJoinListener implements Listener {

	public PlayerJoinListener() {
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(AdvancedCoreLoginEvent event) {
		final Player p = event.getPlayer();

		if (p != null) {
			User user = UserManager.getInstance().getAylaChatUser(p);
			user.checkChannels();
		}

	}

}
