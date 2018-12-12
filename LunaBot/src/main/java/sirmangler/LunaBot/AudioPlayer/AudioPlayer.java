package sirmangler.LunaBot.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import sirmangler.LunaBot.discord.LunaBot;

public class AudioPlayer {

	/**
	 * @author SirMangler (catty610)
	 * 14 May 2018
	 */

	private final AudioPlayerManager playerManager;
	public GuildMusicManager musicManager;
	long guild = 325273265985683457L;
	
	public AudioPlayer() {
		this.playerManager = new DefaultAudioPlayerManager();
		this.musicManager = new GuildMusicManager(playerManager);
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}
	
	public void loadAndPlay(final TextChannel channel, final String trackUrl) {
		
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

				play(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();

				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}

				channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

				musicManager.scheduler.queue.add(firstTrack);
			}

			@Override
			public void noMatches() {
				channel.sendMessage("Nothing found in the URL: " + trackUrl).queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			}
		});
	}

	public void play(AudioTrack track) {
		connectToFirstVoiceChannel(LunaBot.jda.getGuildById(325273265985683457L).getAudioManager());

		musicManager.scheduler.queue(track);
	}

	public void skipTrack(TextChannel channel) {
		musicManager.scheduler.nextTrack();

		channel.sendMessage("Skipped to next track.").queue();
	}
	

	public int tracks() {
		return musicManager.scheduler.queue.size();
	}
	
	private void connectToFirstVoiceChannel(AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			VoiceChannel c = LunaBot.jda.getVoiceChannelById(LunaBot.data.musicChannel);
			audioManager.setSendingHandler(musicManager.getSendHandler());
			audioManager.openAudioConnection(c);
		}
	}
}
