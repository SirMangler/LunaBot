package sirmangler.LunaBot.discord;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class Commands {

	/**
	 * @author SirMangler (catty610)
	 * 2 Apr 2018
	 */

	public static List<Entry<String, Long>> reminders = new LinkedList<Entry<String, Long>>();
	
	public static void updateLevels(MessageReceivedEvent event) {
		if (event.getAuthor().getId().equalsIgnoreCase("176128994176008192") || event.getAuthor().getId().equalsIgnoreCase("167445584142139394")) {
			LunaBot.data.userXP.forEach((id, raw) -> {
				if (raw == null) {
					LunaBot.data.setUserXP(id, 5, 1);
					return;
				}
				
				String[] data = raw.split(":");
				
				long xp = Integer.parseInt(data[0]);
				int level = (int) xp / 500;
				
				LunaBot.data.setUserXP(id, xp, level);
				
				System.out.println("["+id+"] LVL "+level+" ("+xp+")");
			});
			
			event.getChannel().sendMessage("Complete. See console for output.").complete();
		}
	}
	
	public static void reload(MessageReceivedEvent event, LunaBot superStarBot) {
		if (event.getAuthor().getId().equalsIgnoreCase("176128994176008192") || event.getAuthor().getId().equalsIgnoreCase("167445584142139394")) {
			LunaBot.data.loadData();
			delayedDelete(event.getChannel().sendMessage("Successfully reloaded the data file.").complete(), superStarBot);
		}
	}
	
	public static void queueMusic(String message, MessageReceivedEvent event, LunaBot superStarBot) {
		if (event.getChannel().getId().equalsIgnoreCase("451785015953588234") || event.getChannel().getId().equalsIgnoreCase("270234493124608000")) {
			String[] args = message.split(" ", 2);
			LunaBot.audio.loadAndPlay(event.getTextChannel(), args[1]);
		} else {
			delayedDelete(event.getTextChannel().sendMessage("Must be used in the music channel! <#451785015953588234>").complete(), superStarBot);
		}
	}
	
	public static void skipMusic(MessageReceivedEvent event, LunaBot superStarBot) {
		if (event.getChannel().getId().equals("451785015953588234")) {
			LunaBot.audio.skipTrack(event.getTextChannel());
		}
	}
	
	public static void google(String message, MessageReceivedEvent event, LunaBot superStarBot) {
		String query = message.substring(LunaBot.data.prefix.length()+7).replace(" ", "+");
		
		event.getChannel().sendMessage("https://www.google.com/search?q="+query).queue();
	}
	
	public static void ban(String message, MessageReceivedEvent event, LunaBot superStarBot) {
		if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
			if (message.equalsIgnoreCase(LunaBot.data.prefix+"ban")) {
				event.getChannel().sendMessage("I stand ready to bring justice to this land with the pure might of my ban hammer.").complete();
			} else if (!event.getMessage().getMentionedUsers().isEmpty()) {
				Member member = event.getMessage().getMentionedMembers().iterator().next();
				
				String[] split = message.split(" ", 3);
				
				if (event.getGuild().getSelfMember().canInteract(member)) {
					if (split.length == 3) {
						event.getGuild().getController().ban(member, 0, split[2]).complete();
						event.getChannel().sendMessage(member.getEffectiveName()+" has been banned. Reason: "+split[2]).queue();
					} else {
						event.getGuild().getController().ban(member, 0, "Banned by "+event.getAuthor().getName()).complete();
						event.getChannel().sendMessage(member.getEffectiveName()+" has been banned.").queue();
					}
				} else {
					delayedDelete(event.getChannel().sendMessage("I cannot ban that user!").complete(), superStarBot);
				}
			} else {
				delayedDelete(event.getChannel().sendMessage("I cannot find that user!").complete(), superStarBot);
			}
		} else {
			event.getChannel().sendMessage(event.getAuthor().getAsMention()+" I see no reason to answer to you.").complete();
		}
	}
	
	public static void time(String time, String zone1, String zone2, MessageReceivedEvent event) {
		int hours;
		int minutes;
		
		String[] units;
		if (time.contains(":")) 
			units = time.replace("am", "").replace("pm", "").split(":");
		else units = new String[] { time.replace("am", "").replace("pm", ""), "00" };
		
		try {
			hours = Integer.parseInt(units[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			event.getTextChannel().sendMessage("Syntax Example: 9:31am BST GMT").complete();
			return;
		}
		
		if (time.endsWith("pm"))
			hours+=12;
		
		try {
			minutes = Integer.parseInt(units[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			event.getTextChannel().sendMessage("Syntax Example: 9:31am BST GMT").complete();
			return;
		}
		
		Calendar z1 = Calendar.getInstance(TimeZone.getTimeZone(zone1));
		z1.set(Calendar.HOUR, hours);
		z1.set(Calendar.MINUTE, minutes);
		
		Calendar z2 = Calendar.getInstance(TimeZone.getTimeZone(zone2));
		z2.setTimeInMillis(z1.getTimeInMillis());
		
		int im = z2.get(Calendar.MINUTE);
		int ih = z2.get(Calendar.HOUR);
		
		String h = ih+"";
		String m = im+"";
		
		if (im == 0) {
			m = "00";
		}
		
		if (ih == 0) {
			h = "00"+m+"am";
		} else if (ih > 12) {
			h = ih-12+":"+m+"pm";
		} else if (ih < 12) {
			h = ih+":"+m+"am";
		}
		
		event.getTextChannel().sendMessage(time+" "+zone1+" = "+h+": " + zone2).complete();	
	}
	
	public static void purge(String message, MessageReceivedEvent event, LunaBot superStarBot) {
		Permission[] permissions = { Permission.MESSAGE_MANAGE };
		
		if (!message.contains(" ")) {
			boolean hasPermission = PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), permissions);
			if (hasPermission) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.green);
				embed.setTitle("**Purge*");
				embed.setDescription("\n```css\n"+LunaBot.data.prefix
						+"\n purge [amount] [arguments...] \n   /*default: 1*/"
						+"\n   contains [message] \n   /*Messages containing this message*/"
						+"\n   startsWith [message] \n   /*Messages starting with this message*/"
						+"\n   endsWith [message] \n   /*Messages ending with this message*/"
						+"\n   links\n   /*Messages with links*/"
						+"\n   invites\n   /*Messages with invites*/"
						+"\n   images\n   /*Messages with images*/"
						+"\n   mentions\n   /*Messages with mentions*/"
						+"\n   embeds\n   /*Messages with embed*/"
						+"\n   bots\n   /*Messages sent by bots*/"
						+"\n\nExample:\n ?purge 20 startswith \"ninja\" endswith \"sexparty\" links\n/*Can only delete messages up to 2 weeks old.*/```");
				event.getChannel().sendMessage(embed.build()).complete();
			}
		} else {
			boolean hasPermission = PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), permissions);
			if (!hasPermission) {
				delayedDelete(event.getChannel().sendMessage("I require the MESSAGE_MANAGE permission in order to do that!").complete(), superStarBot);
				return;
			}
			
			List<Message> messageHistory = event.getChannel().getHistory().retrievePast(100).complete();
			
			String[] args = message.split(" ");
			int number = 1;
			User user; 
			
			if (event.getMessage().getMentionedUsers().isEmpty()) user = null; 
				else user = event.getMessage().getMentionedUsers().get(0);
			
			String match = null;
			String contains = null;
			String startsWith = null;
			String endsWith = null;
			boolean links = false;
			boolean invites = false;
			boolean images = false;
			boolean mentions = false;
			boolean embeds = false;
			boolean bots = false;
			
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
			
				try {
					number = Integer.parseInt(arg);
					continue;
				} catch (NumberFormatException e) {}
				
				switch (arg.toLowerCase()) {
					/*case "match":
						if (i+1 < args.length) {
							break;
						} else {
							String args2 = args[i+1];
							if (args2.startsWith("\"")) {
								match = message.substring(message.indexOf(args2)).split("\"")[1];
							}
						}*/
					case "contains": 
						if (i+1 > args.length) {
							break;
						} else {
							String args2 = args[i+1];
							if (args2.startsWith("\"")) {
								contains = message.substring(message.indexOf(args2)).split("\"")[1];
							}
						}
						break;
					case "startswith":
						if (i+1 < args.length) {
							break;
						} else {
							String args2 = args[i+1];
							if (args2.startsWith("\"")) {
								startsWith = message.substring(message.indexOf(args2)).split("\"")[1];
							} else {
								startsWith = message.substring(message.indexOf(arg));
								for (Message retrievedMessage : messageHistory) {
									if (retrievedMessage.getContentRaw().startsWith(startsWith)) {
										retrievedMessage.delete().queue();
									}
								}
								
								return;
							}
						}
						break;					
					case "endswith":
						if ((i+1) > args.length) {
							break;
						} else {
							String args2 = args[i+1];
							if (args2.startsWith("\"")) {
								endsWith = message.substring(message.indexOf(args2)).split("\"")[1];
							} else {
								endsWith = message.substring(message.indexOf(arg));
								for (Message retrievedMessage : messageHistory) {
									if (retrievedMessage.getContentRaw().endsWith(endsWith)) {
										retrievedMessage.delete().queue();
									}
								}
								
								return;
							}
						}
						break;
					case "links":
						links = true;
						break;
					case "invites":
						invites = true;
						break;
					case "images":
						images = true;
						break;
					case "mentions":
						mentions = true;
						break;
					case "embeds":
						embeds = true;
						break;
				}
			}
			
			Collection<Message> messages = new ArrayList<Message>();
			int i = 0;
			for (Message retrievedMessage : messageHistory) {
				if (retrievedMessage.getId().equals(event.getMessageId())) continue;
				
				if (number != 0) 
					if (i >= number)
						break;
				
				if (retrievedMessage.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))) {
					continue;
				}
				
				if (user != null) {
					if (!retrievedMessage.getAuthor().equals(user))
						continue;
				}
				
				if (match != null) {
					System.out.println(match);
					if (!retrievedMessage.getContentRaw().equalsIgnoreCase(match))
						continue;
				}
				
				if (startsWith != null) {
					if (!retrievedMessage.getContentRaw().startsWith(startsWith))
						continue;
				}
				if (endsWith != null) {
					if (!retrievedMessage.getContentRaw().endsWith(endsWith))
						continue;
				}
				if (links) {
					if (!retrievedMessage.getContentRaw().matches("(https://|http://)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)"));
						continue;
				}
				if (invites) {
					if (retrievedMessage.getInvites().isEmpty())
						continue;
				}
				if (images) {
					boolean isImage = false;
					Iterator<Attachment> it = retrievedMessage.getAttachments().iterator();
					while (it.hasNext()) {
						Attachment at = it.next();
						if (at.isImage()) {
							isImage = true;
							break;
						}
					}
					
					if (!isImage) continue;
				}
				if (mentions) {
					if (retrievedMessage.getMentionedUsers().isEmpty())
						continue;
				}
				if (embeds) {
					if (retrievedMessage.getEmbeds().isEmpty())
						continue;
				}
				if (contains != null) {
					if (!retrievedMessage.getContentRaw().contains(contains))
						continue;
				}
				if (bots) {
					if (!retrievedMessage.getAuthor().isBot())
						continue;
				}
				
				
				messages.add(retrievedMessage);
				i++;
			}
			
			if (messages.size() == 1)
				event.getTextChannel().deleteMessageById(messages.iterator().next().getId()).queue();
			else event.getTextChannel().deleteMessages(messages).queue();
		}
		
	}

	public static void SexBang(MessageReceivedEvent event, LunaBot superStarBot) {
		if (!event.getTextChannel().getId().equalsIgnoreCase("344320566309814272")) return;
		
		Builder request = new Request.Builder();
		request.url("https://web.stagram.com/rss/n/danny__avidan");
		Request get = request.get().build();
		Response response;
		try {
			response = LunaBot.okclient.newCall(get).execute();
			
			List<String> urls = new ArrayList<>();
			String body = response.body().string();
			
			String[] lines = body.split("<media:thumbnail url=\"");
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i];
				String url = line.substring(0, line.indexOf("\">"));
				urls.add(url);
			}
			
			if (urls.size() != 0) {
				Random r = new Random(); 
				
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.blue);
				embed.setImage(urls.get(r.nextInt(urls.size())));
				
				event.getChannel().sendMessage(embed.build()).queue();
			} else {
				SexBang(event, superStarBot);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	public static double CtoF(double number) {
			try {
				double f = number*9/5+32;
				return f;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
	}

	public static double FtoC(double number) {
		try {
			double c = (number-32)*5/9;
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String[] curriencies = {"AUD", "BRL", "BGN", "CAD", "CNY", "HRK", "CYP", "CZK", "DKK", "EEK", "EUR", "HKD", "HUF","ISK", "IDR", "JPY", "KRW", "LVL", "LTL", "MYR", "MTL", "NZD", "NOK", "PHP", "PLN", "RON", "RUB", "SGD", "SKK", "SIT", "ZAR", "SEK", "CHF", "THB", "TRY", "GBP", "USD" };
	//"https://data.fixer.io/api/convert?access_key=07e463a8745f1a2296b30065029efd2e&from="+from+"&to="+to+"&amount="+amount
	public static double currency(double amount, String to, String from) {
		HttpURLConnection con;
		
		try {
			con = (HttpURLConnection) new URL("https://free.currencyconverterapi.com/api/v5/convert?q="+to+"_"+from+"&compact=ultra?amount="+amount).openConnection();
			//con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			
			StringBuilder builder = new StringBuilder();
			while ((inputLine = in.readLine()) != null) 
				builder.append(inputLine);
			in.close();
			
			String body = builder.toString();
			if (body.isEmpty()) return -1;
			
			System.out.println(body);
			JSONObject obj = new JSONObject(body);
			
			JSONObject results = obj.getJSONObject("results").getJSONObject(from+"_"+to);
			
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			
			return Double.parseDouble(df.format(amount * results.getDouble("val")));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	

		return -1;
	}
	
	public static void set(String message, MessageReceivedEvent event, LunaBot superStarBot) {
		if (event.getAuthor().getId().equalsIgnoreCase("176128994176008192") || event.getAuthor().getId().equalsIgnoreCase("167445584142139394")) {
			if (message.contains(" ")) {
				String[] args = message.split(" ");
				if (args[1].equalsIgnoreCase("prefix")) {
					if (args.length == 2) {
						delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data.prefix+"set prefix [prefix]`").complete(), superStarBot);
						return;
					} else {
						StringBuilder pref = new StringBuilder();
						for (int i = 2; i < args.length; i++) {
							pref.append(args[i]);
						}
						LunaBot.data.setPrefix(pref.toString());
						event.getChannel().sendMessage("The command prefix has been set to: `"+LunaBot.data.prefix+"`").queue();
						return;
					}
				} else if (args[1].equalsIgnoreCase("debugmode")) {
					if (args.length == 2) {
						delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data.prefix+"set debugmode [on:off]`").complete(), superStarBot);
						return;
					} else {
						if (args[2].equalsIgnoreCase("on")) {
							LunaBot.data.setDebugMode(true);
							event.getChannel().sendMessage("Debug mode has been enabled.").queue();
						} else if (args[2].equalsIgnoreCase("off")) {
							LunaBot.data.setDebugMode(false);
							event.getChannel().sendMessage("Debug mode has been disabled.").queue();
						} else {
							delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data.prefix+"set debugmode [on:off]`").complete(), superStarBot);
						}
						
						return;
					}
				} else if (args[1].equalsIgnoreCase("deletedelay")) {
					if (args.length == 2) {
						delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data.prefix+"set deletedelay [seconds]`").complete(), superStarBot);
						return;
					} else {
						try {
							double d = Double.valueOf(args[2]);
							LunaBot.data.setDeleteDelay(d);
							event.getChannel().sendMessage("deleteDelay has been set to "+d+" seconds! :stopwatch:").complete();
						} catch (Exception e) {
							delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data+"set deletedelay [seconds]`").complete(), superStarBot);
						}
						return;
					}
				} else if (args[1].equalsIgnoreCase("botname")) {
					if (args.length == 2) {
						delayedDelete(event.getChannel().sendMessage("**[Syntax]** `"+LunaBot.data.prefix+"set botName [name]`").complete(), superStarBot);
						return;
					} else {
						event.getJDA().getSelfUser().getManager().setName(args[2]).complete();
						event.getChannel().sendMessage("My name has been changed to "+args[2]).queue();
					}
				} else if (args[1].equalsIgnoreCase("announcementChannel")) {
					if (args.length == 3 && !event.getMessage().getMentionedChannels().isEmpty()) {
						TextChannel channel = event.getMessage().getMentionedChannels().get(0);
						LunaBot.data.setAnnouncementChannel(channel.getId());
						event.getChannel().sendMessage("Announcer channel has been set to: "+channel.getAsMention()).queue();
					} else {
						delayedDelete(event.getChannel().sendMessage("Syntax: set announcementChannel [#Channel]").complete(), superStarBot);
					}
				} else if (args[1].equalsIgnoreCase("musicChannel")) {
					if (args.length == 3 && !event.getMessage().getMentionedChannels().isEmpty()) {
						VoiceChannel channel = event.getGuild().getVoiceChannelById(args[2]);
						LunaBot.data.setMusicChannel(channel.getId());
						event.getChannel().sendMessage("Music channel has been set to: "+channel.getName()).queue();
					} else {
						delayedDelete(event.getChannel().sendMessage("Syntax: set musictChannel [channel_id]").complete(), superStarBot);
					}
				} else {
					delayedDelete(event.getChannel().sendMessage("Unknown command.").complete(), superStarBot);
				}
			} else {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.magenta);
				embed.setTitle("**SET - SubCommands**");
				embed.setDescription("\n```css\n"+LunaBot.data.prefix
						+"\n set prefix [prefix] \n   /*Changes the commmand prefix*/ (default: ?) "
						+"\n set debugmode [on:off] \n   /*Toggle debugging messages*/ (default: off)"
						+"\n set deletedelay [seconds] \n   /*delay before info messages are deleted*/ (default: 3)"+""
						+"\n set botname [name] \n   /*Changes the username of the bot.*/"
						+"\n set announcementChannel [@Channel] \n  /*Changes the announcer channel.*/```");
				event.getChannel().sendMessage(embed.build()).queue();
			}
		}
	}
	
	public static void delayedDelete(Message message, LunaBot superStarBot) {
		new Thread(() -> {
			try {	
				long sleep = (long) (LunaBot.data.deleteDelay*1000);
				Thread.sleep(sleep);
				message.delete().queue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();;
	}
}
