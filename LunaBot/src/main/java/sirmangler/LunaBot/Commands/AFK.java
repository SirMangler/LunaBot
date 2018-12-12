package sirmangler.LunaBot.Commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sirmangler.LunaBot.discord.LunaBot;

public class AFK extends Command {

	public AFK(String alias) {
		super(alias);
	}

	public AFK(String[] alias) {
		super(alias);
	}
	
	String[] aliases = new String[] {
		"afk", "brb"
	};

	@Override
	public boolean execute(MessageReceivedEvent event, String[] args) {
		Member member = event.getMember();
		String reason = String.join(" ", args);
		
		String name;
		if (member.getNickname() != null)
			name = member.getNickname();
		else name = member.getEffectiveName();
		
		if (event.getGuild().getSelfMember().canInteract(event.getMember())) {
			String newname = "[AFK] "+name;
			if (newname.length() > 32) {
				newname = newname.substring(0, 32);
			}
			
			event.getGuild().getController().setNickname(event.getMember(), newname).queue();
		} /*else if (SuperStarBot.data.debugmode) {
			if (reason != null) {
				delayedDelete(event.getChannel().sendMessage("[DEBUG] "+name
						+" I cannot nickname you due to a permissions issue, however you are AFK because you're: "+reason).complete(), superStarBot);
			} else {
				delayedDelete(event.getChannel().sendMessage("[DEBUG] "+name
						+" I cannot nickname you due to a permissions issue.").complete(), superStarBot);
			}
		}*/

		event.getChannel().sendMessage("You're now AFK because: "+reason).complete();
		
		LunaBot.data.putAFKMember(event.getAuthor().getId(), reason);
		
		return true;
	}

	@Override
	public boolean noArgs(MessageReceivedEvent event) {
		event.getChannel().sendMessage("You're now AFK.").complete();
		LunaBot.data.putAFKMember(event.getAuthor().getId(), "");
		return true;
	}

}
