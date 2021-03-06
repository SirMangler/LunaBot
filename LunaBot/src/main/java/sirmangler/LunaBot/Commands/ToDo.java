package sirmangler.LunaBot.Commands;

import java.awt.Color;
import java.util.Map.Entry;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class ToDo extends Command {

	private String[] aliases = new String[] {
			"todo"
		};
	
	private String failure = "Unknown arguments. Usage: todo add \"NerdzillaFTW is cute.\"";

	@Override
	public boolean execute(MessageReceivedEvent event, String[] args) {
		if (isStaff(event.getMember(), event.getGuild())) {
			if (args[0].equalsIgnoreCase("add") && args.length > 1) {
				StringBuilder build = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					build.append(args[i]+" ");
				}

				String todo = build.toString().substring(0, build.length()-1);

				LunaBot.data.addToDo(todo);

				event.getChannel().sendMessage("Added \""+todo+"\" to the list.").complete();
				return true;
			} else if (args[0].equalsIgnoreCase("complete")) {
				try {
					int item = Integer.parseInt(args[1]);
					LunaBot.data.completeToDo((item-1));
					event.getChannel().sendMessage("Completed item "+item).complete();
					return true;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					delayedDelete(event.getChannel().sendMessage("Usage: todo complete [index]").complete());
				}
			} else if (args[0].equalsIgnoreCase("clear")) {
				LunaBot.data.clearToDo();
				event.getChannel().sendMessage("Cleared your to-do list.").complete();
				return true;
			} else if (args[0].equalsIgnoreCase("help")) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.green);
				embed.setTitle("**To-Do List**");
				embed.setDescription("\n```css\n"+LunaBot.data.prefix
						+"\n todo add [message] \n   /*Adds a todo to the list.*/"
						+"\n todo complete [index] \n   /*Complete's one of the to-dos'.*/"
						+"\n todo clear \n   /*Clears the to-do list.*/"
						+"\n todo remove [index]\n   /*Removes the selected index*/"
						+"\n todo \n   /*Displays the list.*/```");
				event.getChannel().sendMessage(embed.build()).complete();
				return true;
			} else if (args[0].equalsIgnoreCase("remove")) {
				try {
					int item = Integer.parseInt(args[1]);
					LunaBot.data.removeToDo((item-1));
					event.getChannel().sendMessage("Removed item "+item+" from your to-do list.").complete();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					delayedDelete(event.getChannel().sendMessage("Usage: todo remove [index]").complete());
				}
			}
			
			return true;
		}
		return false;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		if (isStaff(e.getMember(), e.getGuild())) {
			StringBuilder list = new StringBuilder();
			int i = 0;
			for (Entry<String, Boolean> entry : LunaBot.data.toDo.entrySet()) {
				String line = entry.getKey();
				boolean completed = entry.getValue();

				list.append((i+1)+". "+line+" "+(completed ? ":ballot_box_with_check:" : ":regional_indicator_x:")+"\n\n");
				i++;
			}

			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.blue);
			embed.setTitle("**To-Do List**");
			embed.setDescription(list.toString());
			e.getChannel().sendMessage(embed.build()).complete();
			
			return true;
		}
		return false;
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}

	@Override
	public String getFailureResponse() {
		return failure;
	}

}
