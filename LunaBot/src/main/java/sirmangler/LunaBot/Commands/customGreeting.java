package sirmangler.LunaBot.Commands;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class customGreeting extends Command {

	public customGreeting(String[] alias) {
		super(alias);
	}
	
	public customGreeting(String alias) {
		super(alias);
	}

	String[] aliases = new String[] {
			"customgreeting", "cg"
		};
	
	EmbedBuilder embed = new EmbedBuilder().setColor(Color.magenta).setTitle("**Custom Greetings**").setDescription("\n```css\n"+LunaBot.data.prefix+"customgreeting | cg"
			+"\n @User \"[greeting]\" \n   /*Set's a custom greeting*/\n"
			+"\n\n delete @User \n   /*Delete's a user's custom greeting.*/\n```");
	
	@Override
	public boolean execute(MessageReceivedEvent e, String[] args) {
		if (isStaff(e.getMember(), e.getGuild())) {
			if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {//"(?:[^"\\]|\\.)*"
				if (!e.getMessage().getMentionedUsers().isEmpty()) {
					LunaBot.data.removeCustomGreeting(e.getMessage().getMentionedUsers().get(0).getId());
					return true;
				} else {
					delayedDelete(e.getChannel().sendMessage("Couldn't find user!").complete());
					return true;
				}
			}
			
			String greeting = String.join(" ", args);
			if (greeting.contains("\"")) {
				int f = greeting.indexOf('"');
				int l = greeting.lastIndexOf('"');
				if (f != l) {
					greeting = greeting.substring(f+1, l);
					
					LunaBot.data.setCustomGreeting(greeting, e.getMessage().getMentionedUsers().get(0).getId());
					e.getChannel().sendMessage("Set "+e.getMessage().getMentionedUsers().get(0).getAsMention()+"'s custom greeting to `"+greeting+"`").complete();
				}
				return false;
			}				
		}	
		return true;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		if (isStaff(e.getMember(), e.getGuild())) {
			e.getChannel().sendMessage(embed.build()).queue();
		}
		
		return true;
	}

}
