package sirmangler.LunaBot.Commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class SetLevel extends Command {

	private String[] aliases = new String[] { "xp", "level" };
	private String failure = "Unknown arguments. Usage:\nsetXP level @SirMangler 99";
	
	@Override
	public boolean execute(MessageReceivedEvent e, String[] args) {
		Message message = e.getMessage();
		
		if (args.length > 2) {
			String id = e.getMessage().getMentionedUsers().iterator().next().getId();
			if (args[0].equalsIgnoreCase("setlevel")) {
				if (e.getMessage().getMentionedUsers().isEmpty()) {
					delayedDelete(message.getTextChannel().sendMessage("Unknown arguments. Can't find user.").complete());
					return true;
				} else {
					User m = message.getMentionedUsers().iterator().next();
					int level = Integer.parseInt(args[2]);

					long xp = Integer.parseInt(LunaBot.data.userXP.get(id).split(":")[0]);

					LunaBot.data.setUserXP(m.getId(), xp, level);
					message.getTextChannel().sendMessage("Updated "+m.getAsMention()+"'s level to "+level).complete();
					return true;
				}
			} else if (args[0].equalsIgnoreCase("setxp")) {
				if (e.getMessage().getMentionedUsers().isEmpty()) {
					delayedDelete(message.getTextChannel().sendMessage("Unknown arguments. Can't find user.").complete());
					return true;
				} else {
					User m = message.getMentionedUsers().iterator().next();
					long xp = Long.parseLong(args[2]);

					int level = Integer.parseInt(LunaBot.data.userXP.get(id).split(":")[1]);

					LunaBot.data.setUserXP(m.getId(), xp, level);
					message.getTextChannel().sendMessage("Updated "+m.getAsMention()+"'s XP to "+xp).complete();
					return true;
				}
			} 
		} else if (args[0].startsWith("<")) {
			if (e.getMessage().getMentionedUsers().isEmpty()) {
				delayedDelete(message.getTextChannel().sendMessage("Unknown arguments. Can't find user.").complete());
				return true;
			} else {
				String id = e.getMessage().getMentionedUsers().iterator().next().getId();
				String xpraw = LunaBot.data.userXP.get(id);
				if (xpraw == null) {
					message.getTextChannel().sendMessage(message.getAuthor().getName()+" is level zero with no XP.").complete();
					return true;
				}
				
				int level = Integer.parseInt(xpraw.split(":")[1]);
				long xp = Integer.parseInt(xpraw.split(":")[0]);
				
				message.getTextChannel().sendMessage(message.getAuthor().getName()+" is level "+level+" with "+xp+"XP!!").complete();
				return true;
			}
		} else if (args[0].equalsIgnoreCase("leaderboard")) {
			Entry<String, String> tempxp;
			List<Entry<String, String>> et = new ArrayList<>(LunaBot.data.userXP.entrySet());

			for (int i = 0; i < et.size(); i++) {
				for (int j = 1; j <(et.size()-i); j++) {
					long prevxp = Integer.parseInt(et.get(j-1).getValue().split(":")[0]);
					long currentxp = Integer.parseInt(et.get(j).getValue().split(":")[0]);
					if (prevxp < currentxp) {
						tempxp = et.get(j-1);
						et.set(j-1, et.get(j));
						et.set(j, tempxp);
					}
				}
			}

			String d = "";
			for (int i = 0; i < et.size(); i++) {
				if (i > 9) 
					break;
				
				String username = e.getJDA().getUserById(et.get(i).getKey()).getName();

				String[] xpraw = et.get(i).getValue().split(":");
				int level = Integer.parseInt(xpraw[1]);
				long xp = Integer.parseInt(xpraw[0]); 

				d = d+(i+1)+". "+username+"\n **LEVEL "+level+"** *"+xp+"xp*\n\n";
			}

			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.green);
			embed.setTitle("**XP Leaderboard**");
			embed.setDescription(d.toString());
			e.getChannel().sendMessage(embed.build()).complete();
			return true;
		}  else if (args[0].equalsIgnoreCase("list")) {
			if (isStaff(e.getMember(), e.getGuild())) {
				Entry<String, String> tempxp;
				List<Entry<String, String>> et = new ArrayList<>(LunaBot.data.userXP.entrySet());

				for (int i = 0; i < et.size(); i++) {
					for (int j = 1; j <(et.size()-i); j++) {
						long prevxp = Integer.parseInt(et.get(j-1).getValue().split(":")[0]);
						long currentxp = Integer.parseInt(et.get(j).getValue().split(":")[0]);
						if (prevxp < currentxp) {
							tempxp = et.get(j-1);
							et.set(j-1, et.get(j));
							et.set(j, tempxp);
						}
					}
				}

				StringBuilder b = new StringBuilder();
				String d = "";
				for (int i = 0; i < et.size(); i++) {
					String username = e.getJDA().getUserById(et.get(i).getKey()).getName();

					String[] xpraw = et.get(i).getValue().split(":");
					int level = Integer.parseInt(xpraw[1]);
					long xp = Integer.parseInt(xpraw[0]); 

					d = d+(i+1)+". "+username+"\n **LEVEL "+level+"** *"+xp+"xp*\n\n";
				}

				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.blue);
				embed.setTitle("**XP LIST**");
				embed.setDescription(d.toString());
				e.getChannel().sendMessage(embed.build()).complete();
				return true;
			}
			return true;
		} else if (args[0].equalsIgnoreCase("help")) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.green);
			embed.setTitle("**To-Do List**");
			embed.setDescription("\n```css\n"+LunaBot.data.prefix
					+"\n xp setxp @User [xp] \n   /*Set's the user's level to the specified level.*/"
					+"\n xp setxp @User [xp] \n   /*Set's the user's level to the specified amount.*/"
					+"\n xp \n   /*Display's your level/xp*/"
					+"\n /*Alias: level*/```");
			e.getChannel().sendMessage(embed.build()).complete();
			return true;
		}  else {
			delayedDelete(message.getTextChannel().sendMessage("Unknown arguments. Usage:\nsetXP level @SirMangler 99").complete());
			return true;
		}
		return false;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		String id = e.getAuthor().getId();
		Message message = e.getMessage();
		if (LunaBot.data.userXP.containsKey(id)) {
			int level = Integer.parseInt(LunaBot.data.userXP.get(id).split(":")[1]);
			long xp = Integer.parseInt(LunaBot.data.userXP.get(id).split(":")[0]);
			
			message.getTextChannel().sendMessage(message.getAuthor().getAsMention()+" You are level "+level+" with "+xp+"XP!!").complete();
			return true;
		} else {
			message.getTextChannel().sendMessage(message.getAuthor().getAsMention()+" You are level zero with no xp.").complete();
			return true;
		}
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
