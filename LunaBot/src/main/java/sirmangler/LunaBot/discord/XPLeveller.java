package sirmangler.LunaBot.discord;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class XPLeveller {

	HashMap<String, Long> times = new HashMap<String, Long>();
	Random r = new Random();
	public void updateXP(Member member, TextChannel channel) {
		String id = member.getUser().getId();
		if (times.containsKey(id)) {
			long prevtime = times.get(id);
			if (prevtime < (prevtime + 60000)) {
				return;
			} else {
				long xp = LunaBot.data.userXP.get(id).xp;
				int level = LunaBot.data.userXP.get(id).level;
				
				xp += r.nextInt(5) + 5;
				
				if (milestone(xp, level)) {
					LunaBot.data.setUserXP(id, xp, level+1);
					
					EmbedBuilder e = new EmbedBuilder();
					e.setColor(1039370);
					e.setTitle(":arrow_up: LEVEL UP! :arrow_up:");
					e.setDescription("You've leveled up to level 9!! <:YenDax:436202017334231040>");
					channel.sendMessage(e.build()).complete();
				} else {
					LunaBot.data.setUserXP(id, xp, level);
				}
				
				times.put(id, Calendar.getInstance().getTimeInMillis());
			}
		}
	}
	
	
	public boolean milestone(long xp, int level) {
		if (xp > 125*level) {
			return true;
		}
		
		return false;
	}
}

