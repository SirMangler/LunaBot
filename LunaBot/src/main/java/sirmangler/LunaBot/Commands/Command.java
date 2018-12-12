package sirmangler.LunaBot.Commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public abstract class Command {

	String[] aliases;
	String description;
	EmbedBuilder embed;
	
	byte state = -1;
	
	Guild guild = null;
	Role comets = null;
	Role moon = null;
	Role star = null;
	Role aurorarole = null;
	
	public void init() { 
		if (!LunaBot.debug) {
			guild = LunaBot.jda.getGuildById("325273265985683457");
			comets = guild.getRolesByName("The Comets", true).get(0);
			moon = guild.getRolesByName("The Moon", true).get(0);
			star = guild.getRolesByName("The Star", true).get(0);
			aurorarole = guild.getRolesByName("The Aurora", true).get(0);
		}
	}
	
	public Command(String alias) {
		aliases = new String[] { alias };
		init();
	}
	
	public Command(String[] alias) {
		aliases = alias;
		init();
	}
	
	public abstract boolean execute(MessageReceivedEvent e, String[] args);
	
	public abstract boolean noArgs(MessageReceivedEvent e);
	
	public boolean interpret(String message, MessageReceivedEvent e) {
		for (String name : aliases) {
			if (message.startsWith(name)) {
				String arg = message.replaceFirst(name, "").trim();
				
				if (arg.isEmpty()) {
					noArgs(e);
					return true;
				} else {
					execute(e, arg.split(" "));
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return aliases[0];
	}
	//
	
	public boolean isStaff(Member member, Guild guild) {
		if (this.guild == null) {
			if (member.getUser().getId().equalsIgnoreCase("167445584142139394")) {
				return true;
			}
		} else if (member.getRoles().contains(comets) || member.getRoles().contains(moon) || member.getRoles().contains(star)) {
			return true;
		}
		
		return false;
	}
	
	public void delayedDelete(Message message) {
		new Thread(() -> {
			try {	
				long sleep = (long) (LunaBot.data.deleteDelay*1000);
				Thread.sleep(sleep);
				message.delete().queue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();;
	}
}		

