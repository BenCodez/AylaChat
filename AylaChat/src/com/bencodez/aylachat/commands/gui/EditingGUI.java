package com.bencodez.aylachat.commands.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.inventory.BInventory;
import com.bencodez.advancedcore.api.inventory.BInventory.ClickEvent;
import com.bencodez.advancedcore.api.inventory.BInventoryButton;
import com.bencodez.advancedcore.api.inventory.editgui.EditGUI;
import com.bencodez.advancedcore.api.inventory.editgui.EditGUIButton;
import com.bencodez.advancedcore.api.inventory.editgui.valuetypes.EditGUIValueBoolean;
import com.bencodez.advancedcore.api.inventory.editgui.valuetypes.EditGUIValueList;
import com.bencodez.advancedcore.api.inventory.editgui.valuetypes.EditGUIValueNumber;
import com.bencodez.advancedcore.api.inventory.editgui.valuetypes.EditGUIValueString;
import com.bencodez.advancedcore.api.item.ItemBuilder;
import com.bencodez.advancedcore.api.messages.StringParser;
import com.bencodez.advancedcore.api.valuerequest.InputMethod;
import com.bencodez.advancedcore.api.valuerequest.ValueRequestBuilder;
import com.bencodez.advancedcore.api.valuerequest.listeners.StringListener;
import com.bencodez.aylachat.AylaChatMain;
import com.bencodez.aylachat.objects.Channel;
import com.bencodez.aylachat.objects.ChannelHandler;

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
	AylaChatMain plugin = AylaChatMain.plugin;

	/**
	 * Instantiates a new commands.
	 */
	private EditingGUI() {
	}

	public void openChannelGUI(Player player, final Channel channel) {
		EditGUI inv = new EditGUI("Channel: " + channel.getChannelName());

		for (final String str : new String[] { "Format", "Permission" }) {
			inv.addButton(new EditGUIButton(new EditGUIValueString(str, channel.getData().getString(str, "")) {

				@Override
				public void setValue(Player player, String value) {
					channel.setValue(str, value);
					player.sendMessage(StringParser.getInstance().colorize("&cSetting " + str + " to " + value));
					plugin.reload();
				}
			}));
		}

		for (final String str : new String[] { "Bungeecoord", "AutoJoin", "LoadMainChannelCommand",
				"LoadAliasChannelCommands" }) {
			Material material = Material.REDSTONE_BLOCK;
			if (channel.getData().getBoolean(str)) {
				material = Material.EMERALD_BLOCK;
			}

			inv.addButton(new EditGUIButton(new ItemBuilder(material),
					new EditGUIValueBoolean(str, channel.getData().getBoolean(str)) {

						@Override
						public void setValue(Player player, boolean value) {
							channel.setValue(str, value);
							player.sendMessage(StringParser.getInstance().colorize("&cSetting " + str + " to " + value));
							plugin.reload();
						}
					}));
		}

		for (final String str : new String[] { "Distance" }) {
			inv.addButton(new EditGUIButton(new EditGUIValueNumber(str, channel.getData().getInt(str)) {

				@Override
				public void setValue(Player player, Number num) {
					channel.setValue(str, num.intValue());
					player.sendMessage(
							StringParser.getInstance().colorize("&cSetting " + str + " to " + num.intValue()));
					plugin.reload();
				}
			}));
		}

		inv.addButton(new EditGUIButton(new ItemBuilder(Material.BOOK),
				new EditGUIValueList("Aliases", channel.getAliases()) {

					@Override
					public void setValue(Player player, ArrayList<String> value) {
						channel.setValue("Aliases", value);
						player.sendMessage("&cSetting aliases");
					}
				}));

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