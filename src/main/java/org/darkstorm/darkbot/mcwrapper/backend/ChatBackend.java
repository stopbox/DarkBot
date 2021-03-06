package org.darkstorm.darkbot.mcwrapper.backend;

import java.util.HashMap;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.mcwrapper.commands.CommandException;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.event.*;
import org.darkstorm.darkbot.minecraftbot.event.protocol.server.ChatReceivedEvent;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class ChatBackend implements Backend, EventListener {
	private final MinecraftBotWrapper bot;

	private HashMap<String, Integer> off = new HashMap<String, Integer>();
	
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
		int index2 = message.indexOf(" ");
		owner = message.substring(index1, index2);
		if (message.toLowerCase().contains("fuck") 
			|| message.toLowerCase().contains("cunt") 
			|| message.toLowerCase().contains("shit") 
			|| message.toLowerCase().contains("ass") 
			|| message.toLowerCase().contains("bitch") 
			|| message.toLowerCase().contains("nigger")) {
			if (!off.containsKey(owner)) {
				bot.say("Please do not say curse words in chat " + owner + "!");
				off.put(owner, 1);
			} else if (off.get(owner) == 1) {
				bot.say("/mute " + owner + " 5m");
				bot.say("Please do not say curse words in chat " + owner + "!");
				bot.say("You have been muted for 5 minutes");
				off.put(owner, 2);
			} else if (off.get(owner) == 2) {
				bot.say("/mute " + owner + " 30m");
				bot.say("Please do not say curse words in chat " + owner + "!");
				bot.say("You have been muted for 30 minutes");
				off.remove(owner);
				off.put(owner, 3);
			} else if (off.get(owner) == 3) {
				bot.say("/kick " + owner + " Please do not say curse words in chat! -KnightBot");
				off.remove(owner);
				off.put(owner, 4);
			} else if (off.get(owner) == 4) {
				bot.say("/tempban " + owner + " 1d");
				off.remove(owner);
				off.put(owner, 5);
			} else if (off.get(owner) == 5) {
				bot.say("/ban " + owner + " You have ben permantly banned for curseing -KnightBot");
			}
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
