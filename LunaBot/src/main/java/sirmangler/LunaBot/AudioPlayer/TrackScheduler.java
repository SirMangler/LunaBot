package sirmangler.LunaBot.AudioPlayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Game;
import sirmangler.LunaBot.discord.LunaBot;

public class TrackScheduler extends AudioEventAdapter {
	  private final AudioPlayer player;
	  public final BlockingQueue<AudioTrack> queue;
	  /**
	   * @param player The audio player this scheduler uses
	   */
	  public TrackScheduler(AudioPlayer player) {
	    this.player = player;
	    this.queue = new LinkedBlockingQueue<>();
	  }

	  /**
	   * Add the next track to queue or play right away if nothing is in the queue.
	   *
	   * @param track The track to play or add to queue.
	   */
	  public void queue(AudioTrack track) {
	    // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
	    // something is playing, it returns false and does nothing. In that case the player was already playing so this
	    // track goes to the queue instead.
	    if (!player.startTrack(track, true)) {
	      queue.offer(track);
	    }
	  }

	  /**
	   * Start the next track, stopping the current one if it is playing.
	   */
	  public void nextTrack() {
	    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
	    // giving null to startTrack, which is a valid argument and will simply stop the player.
		AudioTrack t = queue.poll();
	    player.startTrack(t, false);
	    LunaBot.jda.getPresence().setGame(Game.listening(t.getInfo().title));
	  }

	  @Override
	  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		  LunaBot.jda.getPresence().setGame(Game.streaming("my thoughts.", "https://www.twitch.tv/kelsilynstar"));

		if (LunaBot.jda.getVoiceChannelById(LunaBot.data.musicChannel).getMembers().size() < 2 || queue.isEmpty()) {
			LunaBot.jda.getAudioManagers().get(0).closeAudioConnection();
			return;
		} else if (endReason.mayStartNext) {
	      nextTrack();
	    }
	  }
	}
