package com.Ben12345rocks.AylaChat.Commands.GUI;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Util.EditGUI.EditGUI;
import com.Ben12345rocks.AdvancedCore.Util.EditGUI.EditGUIButton;
import com.Ben12345rocks.AdvancedCore.Util.EditGUI.EditGUIValueType;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory.ClickEvent;
import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventoryButton;
import com.Ben12345rocks.AdvancedCore.Util.Item.ItemBuilder;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.InputMethod;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.ValueRequestBuilder;
import com.Ben12345rocks.AdvancedCore.Util.ValueRequest.Listeners.StringListener;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Objects.Channel;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;

public class EditingGUI {
	static EditingGUI instance = new EditingGUI();

	/**
	 * Gets the single instance of Commands.
	 *
	 * @return single instance of Commands
	 */
	public static EditingGUI getInstance() {
		return instance;
	}

	/** The plugin. */
	Main plugin = Main.plugin;

	/**
	 * Instantiates a new commands.
	 */
	private EditingGUI() {
	}

	public void openChannelGUI(Player player, final Channel channel) {
		EditGUI inv = new EditGUI("Channel: " + channel.getChannelName());

		for (final String str : new String[] { "Format", "Permission" }) {
			inv.addButton(new EditGUIButton(new ItemBuilder(Material.PAPER), str, channel.getData().getString(str, ""),
					EditGUIValueType.STRING) {

				@Override
				public void setValue(Player player, Object value) {
					channel.setValue(str, value);
					player.sendMessage(StringUtils.getInstance().colorize("&cSetting " + str + " to " + value));
					plugin.reload();
				}
			});
		}

		for (final String str : new String[] { "Bungeecoord", "AutoJoin", "LoadMainChannelCommand",
				"LoadAliasChannelCommands" }) {
			Material material = Material.REDSTONE_BLOCK;
			if (channel.getData().getBoolean(str)) {
				material = Material.EMERALD_BLOCK;
			}

			inv.addButton(new EditGUIButton(new ItemBuilder(material), str, channel.getData().getBoolean(str),
					EditGUIValueType.BOOLEAN) {

				@Override
				public void setValue(Player player, Object value) {
					channel.setValue(str, value);
					player.sendMessage(StringUtils.getInstance().colorize("&cSetting " + str + " to " + value));
					plugin.reload();
				}
			});
		}

		for (final String str : new String[] { "Distance" }) {

			inv.addButton(new EditGUIButton(new ItemBuilder(Material.STONE), str, channel.getData().getInt(str),
					EditGUIValueType.NUMBER) {

				@Override
				public void setValue(Player player, Object value) {
					channel.setValue(str, (int) value);
					player.sendMessage(StringUtils.getInstance().colorize("&cSetting " + str + " to " + value));
					plugin.reload();
				}
			});
		}

		inv.addButton(new EditGUIButton(new ItemBuilder(Material.BOOK), "Aliases", channel.getAliases(),
				EditGUIValueType.LIST) {

			@SuppressWarnings("unchecked")
			@Override
			public void setValue(Player player, Object value) {
				channel.setValue("Aliases", (ArrayList<String>) value);
				player.sendMessage("&cSetting aliases");
			}
		});

		inv.openInventory(player);

	}

	public void openGUI(Player player) {
		BInventory inv = new BInventory("Channels");

		for (Channel ch : ChannelHandler.getInstance().getChannels()) {
			BInventoryButton b = new BInventoryButton(new ItemBuilder(Material.STONE).setName(ch.getChannelName())) {

				@Override
				public void onClick(ClickEvent event) {
					openChannelGUI(event.getPlayer(), (Channel) event.getButton().getData().get("Handle"));
				}
			};
			b.addData("Channel", ch);
			inv.addButton(b);
		}

		inv.addButton(new BInventoryButton(new ItemBuilder(Material.PAPER).setName("&cCreate")
				.addLoreLine("&cCreate channel, set name").addLoreLine("&cEdit other values afterwords")) {

			@Override
			public void onClick(ClickEvent event) {
				new ValueRequestBuilder(new StringListener() {

					@Override
					public void onInput(Player player, String value) {
						ChannelHandler.getInstance().create(value);
						openGUI(player);
					}
				}, new String[] {}).usingMethod(InputMethod.CHAT).allowCustomOption(true).currentValue("")
						.request(event.getPlayer());
			}

		});

		inv.openInventory(player);
	}
}