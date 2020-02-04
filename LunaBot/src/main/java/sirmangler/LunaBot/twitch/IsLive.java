package sirmangler.LunaBot.twitch;

import java.io.IOException;

import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import sirmangler.LunaBot.discord.LunaBot;

public class IsLive {

	boolean live = false;

	public IsLive() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				check();
			}
		}).start();
	}
	
	public void check() {
		Builder builder = new Builder().url("https://api.twitch.tv/kraken/streams/51375532");
		builder.addHeader("Accept", "application/vnd.twitchtv.v5+json");
		builder.addHeader("Client-ID", "294fvy3epe78czy1bvi8rilcw5x0ai");
		Request r = builder.get().build();

		try {
			Response resp = LunaBot.okclient.newCall(r).execute();
			JSONObject obj = new JSONObject(resp.body().string());
			
			if (obj.isNull("stream")) {
				live = false;
				return;
			} else {			
				if (live == false) {
					JSONObject stream  = obj.getJSONObject("stream");
					live = true;
					EmbedBuilder embed = new EmbedBuilder();
					embed.setColor(6570405);
					embed.setTitle("**KelsiLynStar has gone live!**");
					embed.setDescription("https://www.twitch.tv/kelsilynstar \n"+stream.getJSONObject("channel").getString("status"));
					embed.addField("Playing", stream.getString("game"), false);
					//embed.addField("Started At (Streamer Time)", , true);
					embed.setImage(stream.getJSONObject("preview").getString("large"));
					//embed.setDescription("\n"+stream.getJSONObject("channel").getString("status")+"\n\nStar has gone live playing: "+stream.getString("game")+"\nhttps://www.twitch.tv/kelsilynstar");
					LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(embed.build()).queue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

	