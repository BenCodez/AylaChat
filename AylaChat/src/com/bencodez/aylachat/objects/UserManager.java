package com.bencodez.aylachat.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.misc.PlayerUtils;
import com.bencodez.advancedcore.api.user.AdvancedCoreUser;
import com.bencodez.aylachat.AylaChatMain;

public class UserManager {
	/** The instance. */
	static UserManager instance = new UserManager();

	/**
	 * Gets the single instance of UserManager.
	 *
	 * @return single instance of UserManager
	 */
	public static UserManager getInstance() {
		return instance;
	}

	public UserManager() {
		super();
	}

	public ArrayList<String> getAllUUIDs() {
		return AylaChatMain.plugin.getUserManager().getAllUUIDs();
	}

	public AylaChatUser getAylaChatUser(AdvancedCoreUser user) {
		return new AylaChatUser(user);
	}

	public AylaChatUser getAylaChatUser(Player player) {
		return getAylaChatUser(player.getName());
	}

	public AylaChatUser getAylaChatUser(String playerName) {
		return getAylaChatUser(UUID.fromString(PlayerUtils.getInstance().getUUID(playerName)));
	}

	@SuppressWarnings("deprecation")
	public AylaChatUser getAylaChatUser(UUID uuid) {
		return new AylaChatUser(uuid);
	}
}
