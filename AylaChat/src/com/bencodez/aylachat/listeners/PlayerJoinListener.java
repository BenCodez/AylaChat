package com.bencodez.aylachat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bencodez.advancedcore.listeners.AdvancedCoreLoginEvent;
import com.bencodez.aylachat.objects.AylaChatUser;
import com.bencodez.aylachat.objects.UserManager;

public class PlayerJoinListener implements Listener {

	public PlayerJoinListener() {
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(AdvancedCoreLoginEvent event) {
		final Player p = event.getPlayer();

		if (p != null) {
			AylaChatUser user = UserManager.getInstance().getAylaChatUser(p);
			user.checkChannels();
		}

	}

}
