package sirmangler.LunaBot.Commands;

import java.util.Date;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.Commands;
import sirmangler.LunaBot.discord.LunaBot;

public class remindMe extends Command {

	private String[] aliases = new String[] { "remindme" };
	private String failure = "Unknown arguments. Usage: remindme to eat in 5m";
	
	@Override
	public boolean execute(MessageReceivedEvent event, String[] args) {
		if (isStaff(event.getMember(), event.getGuild())) {
			String message = String.join(" ", args);
			if (args.length==2) {
				if (args[1].equalsIgnoreCase("clear")) {
					Commands.reminders.clear();
					LunaBot.data.clearReminders();

					System.out.println("Cleared");
					return true;
				} else if (args[1].equalsIgnoreCase("help")) {
					event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>\n       remindme clear").complete();
					return true;
				}
			}

			long duration = 0;				
			String split = message.substring(0, message.lastIndexOf("in"));

			if (split.isEmpty() || !message.contains("in")) {
				delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
				return true;
			}

			String[] timeArgs = message.replace(split, "").split(" ");
			System.out.println(split);
			for (int i = 0; i < timeArgs.length; i++) {
				if (timeArgs[i].endsWith("s")) {
					try {
						String a = timeArgs[i].substring(0, timeArgs[i].indexOf("s"));
						long number = Long.parseLong(a);
						duration += number * 1000L;
					} catch (Exception e) {
						delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
						return false;
					}
				} else if (timeArgs[i].endsWith("m")) {
					try {
						String a = timeArgs[i].substring(0, timeArgs[i].indexOf("m"));
						long number = Long.parseLong(a);
						number *= 60L;
						number *= 1000L;
						duration = duration + number;
					} catch (Exception e) {
						delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
						return false;
					}
				} else if (timeArgs[i].endsWith("h")) {
					try {
						String a = timeArgs[i].substring(0, timeArgs[i].indexOf("h"));
						long number = Long.parseLong(a);
						duration += ((number * 60L) * 60L) * 1000L;
					} catch (Exception e) {
						delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
						return true;
					}
				} else if (timeArgs[i].endsWith("d")) {
					try {
						String a = timeArgs[i].substring(0, timeArgs[i].indexOf("d"));
						long number = Long.parseLong(a);
						duration += (((number * 60L) * 60L) * 24L) * 1000L;
					} catch (Exception e) {
						delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
						return true;
					}
				} 
			}

			if (duration == 0) {
				delayedDelete(event.getChannel().sendMessage("Usage: remindme [message] <{number}s> <{number}m> <{number}h> <{number}d>").complete());
				return true;
			}

			System.out.println("dur: "+duration);
			long time = new Date().getTime();
			System.out.println("tim: "+time);

			time += duration;
			System.out.println("dur+tim: "+time);

			LunaBot.data.addRemindMe(split, time);

			event.getChannel().sendMessage("I will remind you of that in "+duration/1000+"s.").complete();
		}
		return false;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent e) {
		// TODO Auto-generated method stub
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
