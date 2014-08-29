package org.darkstorm.darkbot.mcwrapper.backend;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.mcwrapper.commands.CommandException;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.ChatReceivedEvent;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class ChatBackend implements Backend, EventListener {
	private final MinecraftBotWrapper bot;

	private String activator = "!";

	public ChatBackend(MinecraftBotWrapper bot) {
		this.bot = bot;
	}

	@Override
	public void enable() {
		MinecraftBot mcbot = bot.getBot();
		mcbot.getEventBus().register(this);
	}

	@Override
	public void say(String message) {
		bot.getBot().say(message);
	}

	@Override
	public void disable() {
		MinecraftBot mcbot = bot.getBot();
		mcbot.getEventBus().unregister(this);
	}

	@EventHandler
	public void onChat(ChatReceivedEvent event) {
		String message = Util.stripColors(event.getMessage());
		String owner;
		int index1 = 0;//message.indexOf("]") + 1;
		int index2 = message.indexOf(" ") - 1;
		owner = message.substring(index1, index2);
		if (message.toLowerCase().contains("fuck")) {
			bot.say("Please do not say curse words in chat" + owner + "!");
		}
	}
	
	@EventHandler
	public void onChatReceived(ChatReceivedEvent event) {
		String message = Util.stripColors(event.getMessage());
		String executor = null;
		for(String owner : bot.getOwners()) {
			int index = message.indexOf(owner);
			if(index == -1)
				continue;
			executor = owner;
		}
		if(executor == null)
			return;
		message = message.substring(message.indexOf(executor) + executor.length());
		int index = message.indexOf(activator);
		if(index == -1)
			return;
		message = message.substring(index + activator.length());
		try {
			bot.getCommandManager().execute(message);
		} catch(CommandException e) {
			StringBuilder error = new StringBuilder("Error: ");
			if(e.getCause() != null)
				error.append(e.getCause().toString());
			else if(e.getMessage() == null)
				error.append("null");
			if(e.getMessage() != null) {
				if(e.getCause() != null)
					error.append(": ");
				error.append(e.getMessage());
			}
			bot.getBot().say(error.toString());
		}
	}

	public String getActivator() {
		return activator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}
}
