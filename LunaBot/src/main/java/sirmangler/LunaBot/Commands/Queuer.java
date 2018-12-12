package sirmangler.LunaBot.Commands;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class Queuer extends Command {

	String[] aliases = new String[] {
			"me"
		};
	
	public Queuer(String alias) {
		super(alias);
	}
	
	public Queuer(String[] alias) {
		super(alias);
	}

	
	
	Queue<String> q = new LinkedList<String>();
	Queue<String> aurora = new LinkedList<String>(); 

	@Override
	public boolean execute(MessageReceivedEvent event, String[] args) {
		if (aurorarole == null) {
			event.getChannel().sendMessage("Aurora role is null (debug mode probably enabled). Queuer unavailable.").complete();
		}
		
		if (isStaff(event.getMember(), event.getGuild())) {
			if (args[0].equalsIgnoreCase("clear")) {
				q.clear();
				event.getChannel().sendMessage("The queue has been cleared!").queue();
			} else if (args[0].equalsIgnoreCase("list")) {
				if (q.size() == 0 && aurora.size() == 0) {
					event.getChannel().sendMessage("The queue is empty!").queue();
					return true;
				}

				StringBuilder builder = new StringBuilder();

				int iq;
				for (iq = 0; iq < q.size(); iq++) {
					String a = (String) q.toArray()[iq];
					builder.append("["+iq+"] "+a+"\n");
				}

				for (int i = 0; i < aurora.size(); i++) {
					String a = (String) aurora.toArray()[i];
					builder.append("["+(i+iq)+"] "+a+"\n");
				}

				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.orange);
				embed.setTitle("**Queue List**");
				embed.setDescription(builder.toString());
				event.getChannel().sendMessage(embed.build()).queue();
			} else if (args[0].equalsIgnoreCase("poll")) {
				String next;
				if (!aurora.isEmpty()) {
					next = aurora.poll();
				} else if (!q.isEmpty()) {
					next = q.poll();
				} else {
					next = null;
				}

				if (next != null)
					event.getChannel().sendMessage("Next up is: "+next).queue();
				else
					event.getChannel().sendMessage("The queue is empty!").queue();
			} else if (args[0].equalsIgnoreCase("remove")) {
				String name = args[2];
				if (q.contains(name)) {
					q.remove(name);
					event.getChannel().sendMessage(name+" has been removed from the queue!").queue();
				} else if (aurora.contains(name)) {
					q.remove(name);
					event.getChannel().sendMessage(name+" has been removed from the queue!").queue();
				} else {
					delayedDelete(event.getChannel().sendMessage(name+" is not in the queue!").complete());
				}	
			}
		}
		
		if (args[0].equalsIgnoreCase("next")) {
			if (event.getMember().getRoles().contains(aurorarole)) {
				if (!aurora.contains(event.getAuthor().getName())) {
					aurora.add(event.getAuthor().getName());
					event.getChannel().sendMessage("Added you to the queue with Aurora priority!").queue();
					return true;
				}
			} else {
				if (!q.contains(event.getAuthor().getName())) {
					q.add(event.getAuthor().getName());
					event.getChannel().sendMessage("Added you to the queue!").queue();
					return true;
				}
			}
		}
			
		return false;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		if (isStaff(e.getMember(), e.getGuild())) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.magenta);
			embed.setTitle("**ME - SubCommands**");
			embed.setDescription("\n```css\n"+LunaBot.data.prefix
					+"\n me next \n   /*Adds yourself to the queue*/"
					+"\n me remove [username] \n   /*Removes the exact user from the queue*/"
					+"\n me clear \n   /*Clears the queue*/"+""
					+"\n me list \n   /*Displays the queue in list form*/"
					+"\n me poll \n  /*Gives the next in the queue*/```");
			e.getChannel().sendMessage(embed.build()).queue();
		} else {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.magenta);
			embed.setTitle("**ME - SubCommands**");
			embed.setDescription("\n```css\n"+LunaBot.data.prefix
					+"\n me next \n   /*Adds yourself to the queue*/```");
			e.getChannel().sendMessage(embed.build()).queue();
		}
		
		return false;
	}

}
