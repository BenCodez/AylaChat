package com.Ben12345rocks.AylaChat.Commands.Executors;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.Channel;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;

public class ChannelCommands extends BukkitCommand {
	private Channel ch;

	public ChannelCommands(String name, Channel ch) {
		super(name);
		description = "ValueRequestInput";
		setAliases(new ArrayList<String>());
		this.ch = ch;
	}

	@Override
	public boolean execute(final CommandSender sender, String alias, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

				@Override
				public void run() {
					User user = UserManager.getInstance().getAylaChatUser(player);
					if (args.length == 0) {
						user.setCurrentChannel(ch.getChannelName());
						sender.sendMessage("Channel set");
					} else {
						String msg = ArrayUtils.getInstance().makeString(1, args);

						ChannelHandler.getInstance().onChat(player, ch.getChannelName(), msg);
					}

				}
			});

			return true;
		}
		sender.sendMessage("Must be a player to use this");

		return true;
	}
}