package sirmangler.LunaBot.Commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class Info extends Command {

	public Info(String alias) {
		super(alias);
		// TODO Auto-generated constructor stub
	}
	
	public Info(String[] alias) {
		super(alias);
		// TODO Auto-generated constructor stub
	}

	String[] aliases = new String[] {
			"commands", "cmds", "info"
	};

	@Override
	public boolean execute(MessageReceivedEvent e, String[] args) {	
		return noArgs(e);
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		if (isStaff(e.getMember(), e.getGuild())) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.cyan);
			embed.setTitle("**Commands <Admins>**");
			embed.setDescription("\n```css\n"+LunaBot.data.prefix
					+"\n set \n   /*Allows you to change the bot's settings!*/\n     (default: star) "
					+"\n\n afk <reason> \n   /*Set's you as AFK*/\n    (default: everyone)"
					+"\n\n brb <reason> \n   /*Alias of AFK*/"+""
					+"\n\n reload \n   /*Reloads the bot's data file*/\n    (default: star)"
					+"\n\n purge <variables..> \n  /*Purge messages in a channel*/\n    (default: Manage_Messages Permission)"
					+"\n\n todo \n  /*Star's to-do list*/\n    (default: The Comets)"
					+"\n\n remindMe \n  /*Star's reminders*/\n    (default: The Comets)"
					+"\n\n [number] C \n  /*Converts the number to Celcius*/\n    (default: everyone)"
					+"\n\n [number] F \n  /*Converts the number to Fahrenheit*/\n    (default: everyone)"
					+"\n\n [number] [currency] \n  /*Converts to specified currency*/\n    (default: everyone)"
					+"\n\n sexbang \n  /*The best command*/\n    (default: #anons-hubcap-stash)"
					+"\n\n commands \n  /*Displays this.*/\n    (default: everyone)"
					+"\n\ngoogle [search] \n  /*Googles something for you*/\n    (default: everyone)"
					+"\n\nban [user] <reason>"
					+"\n\nme \n  /*Displays the commands for the Queuer*/\n    (default: everyone)```");
			e.getChannel().sendMessage(embed.build()).queue();
		} else {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.cyan);
			embed.setTitle("**Commands**");
			embed.setDescription("\n```css\n"+LunaBot.data.prefix
					+"\n afk <reason> \n   /*Set's you as AFK*/"
					+"\n\n brb <reason> \n   /*Alias of AFK*/"+""
					+"\n\n [number] C \n  /*Converts the number to Celcius*/"
					+"\n\n [number] F \n  /*Converts the number to Fahrenheit*/"
					+"\n\n [number] [currency] \n  /*Converts to specified currency*/"
					+"\n\n sexbang \n  /*The best command*/"
					+"\n\n commands \n  /*Displays this.*/"
					+"\n\ngoogle [search] \n  /*Googles something for you*/\n    (default: everyone)"
					+"\n\nme \n  /*Displays the commands for the Queuer*/\n    (default: everyone)```");
			e.getChannel().sendMessage(embed.build()).queue();
		}
		return true;
	}
}
