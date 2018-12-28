package sirmangler.LunaBot.discord;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message.MentionType;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import sirmangler.LunaBot.AI.AI;
import sirmangler.LunaBot.AudioPlayer.AudioPlayer;
import sirmangler.LunaBot.Commands.AFK;
import sirmangler.LunaBot.Commands.Command;
import sirmangler.LunaBot.Commands.Info;
import sirmangler.LunaBot.Commands.Queuer;
import sirmangler.LunaBot.Commands.SetLevel;
import sirmangler.LunaBot.Commands.ToDo;
import sirmangler.LunaBot.Commands.customGreeting;
import sirmangler.LunaBot.Commands.remindMe;
import sirmangler.LunaBot.twitch.FollowAlert;
import sirmangler.LunaBot.twitch.IsLive;
import sirmangler.LunaBot.twitch.TwitchIRC;
import sirmangler.LunaBot.twitch.TwitchPubSub;

public class LunaBot extends ListenerAdapter {
	
	/**
	 * @author SirMangler (catty610)
	 * 8 Feb 2017
	 */
	
	public static Data data = new Data();
	public static AI ai = new AI();
	public static OkHttpClient okclient = new OkHttpClient();
	public static JDA jda;
	public static AudioPlayer audio;
	public static XPLeveller xpLeveller = new XPLeveller();
	
	public static void main(String[] args) {
		try {
			System.setErr(new PrintStream(System.err) {
				PrintStream file = new PrintStream("errOut.log");

			    @Override
			    public void flush() {
			        super.flush();
			        file.flush();
			    }

			    @Override
			    public void write(byte[] buf, int off, int len) { 
			        System.out.print(new String(buf, off, len));
			        file.write(buf, off, len);
			    }

			    @Override
			    public void write(int b) {
			        System.err.print(b);
			        file.write(b);
			    }

			    @Override
			    public void write(byte[] b) throws IOException {
			    	System.err.print(b);
			        file.write(b);
			    }
			});
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println(args);
					
					if (args.length > 0)
						debug = args[0].equalsIgnoreCase("debug");
					else debug = false;

					@SuppressWarnings("unused")
					LunaBot superStarBot = new LunaBot(debug);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}
	
	public static boolean debug;
	public LunaBot(boolean debug) {
		try {
			jda = new JDABuilder(AccountType.BOT)
				.setToken(debug ? "NDM5NzgzNzEwOTEzNTkzMzQ2.DcYMBA.h-b0sOOtKIDOehIzDCvj0597eoI" : "NDI0MzE5MzA0NjQ1NzM4NTI3.DY3JuA.G6Scd2RWy6E6_EQUVzGOUhprugU").buildBlocking();
			
			jda.addEventListener(this);		
			
			jda.getPresence().setGame(Game.streaming("my thoughts.", "https://www.twitch.tv/kelsilynstar"));
			
			Thread irc = new Thread(new TwitchIRC());
			Thread pubsub = new Thread(new TwitchPubSub());
			Thread followalert = new Thread(() -> new FollowAlert());
			
			if (!debug) {
				irc.start();
				new IsLive();
				pubsub.start();
				followalert.start();
			} else {
				System.out.print("DEBUG MODE ENABLED");
			}

			audio = new AudioPlayer();
			
			cmds = new Command[] {
					new AFK(), new customGreeting(), new Info(), new Queuer(), new remindMe(), new ToDo(), new SetLevel() 
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
/*	@Override
	public void onGenericUserPresence(final GenericUserPresenceEvent event) {
		if (event.getUser().getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())) {
			jda.getPresence().setGame(Game.streaming("my thoughts.", "https://www.twitch.tv/kelsilynstar"));
		}
	}*/
	
	@Override
	public void onMessageReactionAdd(final MessageReactionAddEvent event) {
		if (event.getMessageId().equalsIgnoreCase("503746848553566230")) {
			if (event.getReactionEmote().getName().equalsIgnoreCase("🍆")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("503740110488076289")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("awoo")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("503740078498250774")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("🏒")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("503739976614281227")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("🍹")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("503740405607694386")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("🔴")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("341283533345652737")).queue();
			}
		} else if (event.getMessageId().equalsIgnoreCase("524396799243583492")) {
			if (event.getReactionEmote().getName().equalsIgnoreCase("switch")) {
				event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("524396263022657536")).queue();
			}
		}
	}
	
	@Override
	public void onMessageReactionRemove(final MessageReactionRemoveEvent event) {
		if (event.getMessageId().equalsIgnoreCase("503746848553566230")) {
			if (event.getReactionEmote().getName().equalsIgnoreCase("eggplant")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("503740110488076289")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("awoo")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("503740078498250774")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("hockey")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("503739976614281227")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("tropical_drink")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("503740405607694386")).queue();
			} else if (event.getReactionEmote().getName().equalsIgnoreCase("red_circle")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("341283533345652737")).queue();
			}
		} else if (event.getMessageId().equalsIgnoreCase("524396799243583492")) {
			if (event.getReactionEmote().getName().equalsIgnoreCase("switch")) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById("524396263022657536")).queue();
			}
		}
	}
	
	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
		event.getGuild().getTextChannelById("325276999947911168").sendMessage("Welcome to Asteroid Belt, "+event.getMember().getAsMention()+"!").complete();	
		event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(325273929944137728L)).queue();
	}
	
	@Override
	public void onGuildMemberLeave(final GuildMemberLeaveEvent event) {
		event.getJDA().getUserById("1761289941760081922").openPrivateChannel().complete().sendMessage(event.getUser().getName()+"#"+event.getUser().getDiscriminator()+" ("+event.getUser().getId()+") has left!").complete();	
	}
	
	Command[] cmds;
	
	@Override
	public void onMessageReceived(final MessageReceivedEvent event)
	{
		String message = event.getMessage().getContentRaw();
		System.out.println("["+new Date().toString()+"] <"+event.getChannel().getName()+"> "+event.getAuthor().getName()+": "+message);
		
		if (!event.getAuthor().isBot()) xpLeveller.updateXP(event.getMember(), event.getTextChannel());
		
		if (message.startsWith(data.prefix)) {
			
			for (Command cmd : cmds) {
				if (cmd.interpret(message.replace(data.prefix, ""), event)) {
					return;
				}
			}
			
			String command;
			String[] split = message.split(" ");
			if (message.contains(" ")) command = split[0].substring(1).toLowerCase();
			else command = message.substring(1).toLowerCase();
			
			switch (command) {
				case "reload":
					Commands.reload(event, this);
					break;
				case "set":
					Commands.set(message, event, this);
					break;
				case "purge":
					Commands.purge(message, event, this);
					break;
				case "sexbang":
					Commands.SexBang(event, this);
					break;
				case "google":
					Commands.google(message, event, this);
					break;
				case "playsong":
					Commands.queueMusic(message, event, this);
					break;
				case "skipsong":
					Commands.skipMusic(event, this);
					break;
				case "play":
					Commands.queueMusic(message, event, this);
					break;
				case "skip":
					Commands.skipMusic(event, this);
					break;
				case "ban": 
					Commands.ban(message, event, this);
					break;
				case "level":
					Commands.level(event);
					break;
			}
			
			if (split.length == 3) {
				for (String currency : Commands.curriencies) {
					if (command.equalsIgnoreCase(currency)) {
						String from = currency;
						
						double d;
						BigDecimal number;
						try {
							number = new BigDecimal(split[1]); 
							number.stripTrailingZeros().round(MathContext.UNLIMITED);
							d = number.doubleValue();
						} catch (NumberFormatException e) {
							System.out.println(split[1]);
							continue;
							
						}

						for (String currencynext : Commands.curriencies) {
							if (split[2].equalsIgnoreCase(currencynext)) {
								EmbedBuilder embed = new EmbedBuilder();
								embed.setColor(Color.green);
								embed.setTitle(from+" to "+currencynext);
								embed.setDescription(":moneybag: "+number.toPlainString()+" "+from+" equals "+Commands.currency(d, from, currencynext)+" "+currencynext);
								event.getChannel().sendMessage(embed.build()).queue();
							}
						}
					}
				}
				
				if (split[0].endsWith("am") || split[0].endsWith("pm") || split[0].contains(":")) {
					Commands.time(split[0].substring(1), split[1], split[2], event);
				}
			}
			
			if (split.length == 2) {
				if (Character.isDigit(command.charAt(0)) || command.charAt(0) == '-') {
					StringBuilder builder = new StringBuilder();
					boolean decimal = false;
					for (int i = 0; i < command.length(); i++) {
						char c = command.charAt(i);
						if (Character.isDigit(c) || c == '-') {
							builder.append(c);
						} else if (c == '.') {
							if (decimal != true) { 
								builder.append(c);
								decimal = true;
							}
						} else {
							builder = null;
							break;
						}
					}
					
					if (builder != null) {
						double number = Double.valueOf((builder.toString()));
						
						DecimalFormat df = new DecimalFormat("#.##");
						df.setRoundingMode(RoundingMode.CEILING);
						
						if (split[1].equalsIgnoreCase("C")) {
							double value = Double.parseDouble(df.format(Commands.CtoF(number)));
							
							EmbedBuilder embed = new EmbedBuilder();
							embed.setColor(Color.red);
							embed.setTitle("C to F");
							embed.setDescription(":thermometer: "+number+"C is equal to "+value+"F");
							
							event.getChannel().sendMessage(embed.build()).queue();
						} else if (split[1].equalsIgnoreCase("F")) {
							double value = Double.parseDouble(df.format(Commands.FtoC(number)));
							
							EmbedBuilder embed = new EmbedBuilder();
							embed.setColor(Color.red);
							embed.setTitle("F to C");
							embed.setDescription(":thermometer: "+number+"F is equal to "+value+"C");
							
							event.getChannel().sendMessage(embed.build()).queue();
						}
					}
				}
			}
		}
		
		if (!event.getAuthor().isBot()) {
			Iterator<Entry<String, String>> it = data.afkmembers.entrySet().iterator();
			
			while (it.hasNext()) {
				Entry<String, String> ent = it.next();
				String memberid = ent.getKey();
				String response = ent.getValue();
				
				Member member = event.getMember();	
				String name;
				if (member.getNickname() != null)
					name = member.getNickname();
				else name = member.getEffectiveName();
				
				if (member.getUser().getId().equals(memberid)) {
					if (event.getGuild().getSelfMember().canInteract(event.getMember()))
						event.getGuild().getController().setNickname(event.getMember(), name.replace("[AFK] ", "")).queue();
					
					Commands.delayedDelete(event.getChannel().sendMessage(name+" is no longer afk. Welcome back!").complete(), this);
					data.removeAFKMember(memberid);
					return;
				}

				if (event.getMessage().isMentioned(event.getJDA().getUserById(memberid), MentionType.USER)) {
					name = name.replaceFirst("[AFK] ", "");
					if (response == "") {
						event.getChannel().sendMessage(event.getAuthor().getAsMention()+" "+name+" is AFK currently.").complete();
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention()+" "+name+" is AFK because they're: "+response).complete();
					}
				}
			}
			
			ai.replyTo(message, event.getAuthor(), event.getTextChannel());
		}
	}
}
