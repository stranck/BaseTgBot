package ovh.stranck.BaseTgBot;

import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class Telegram implements Runnable{
	
	public static final int OTHERS = 0;
	public static final int CALLBACK = 1;
	public static final int MESSAGE = 2;
	
	public TelegramBot bot;
	private int offset = 0;
	
	public Telegram(){
		App.LOGGER.config("Loading telegram bot");
    	bot = new TelegramBot(Settings.telegramToken);
    	Settings.botUser = bot.execute(new GetMe()).user().username();
    	new Thread(this).start();		
	}

	public void run() {
		GetUpdatesResponse updatesResponse;
		List<Update> updates;
		while(true){
			try{
				updatesResponse = bot.execute(new GetUpdates().offset(offset));
				updates = updatesResponse.updates();
				for(Update update : updates) {
					offset = update.updateId() + 1;
					new Thread(){
						public void run(){
							try {
								int type = getUpdateType(update);
								String text = getText(update, type);
								App.LOGGER.fine(getFromId(update, type) + ">[" + type + "]\t" + text);
								//if(Settings.hasPermissions(getFromId(update, type))){
									if(type > 0 && type < 3){
										long chatId = getChatid(update, type);
										
										if(Settings.maintenance && !Settings.isAdmin(chatId)){
											bot.execute(new SendMessage(chatId, "⚠️ The bot is under maintainance. Retry later with /start")
													.parseMode(ParseMode.Markdown).disableWebPagePreview(true)
													.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[][] {{
														new InlineKeyboardButton("📰 Join the channel 📰").url(Settings.channel),
														new InlineKeyboardButton("👥 Join the group 👥").url(Settings.group)
													}})));
										} else {
											Command cmd = Command.getCommand(text, Settings.botUser, type, true);
											if(cmd != null){
												String txt = cmd.execute(bot, update);
												if(!text.equals("")){
													if(cmd.isPhoto()){
														SendPhoto sm;
														if(cmd.isOnlinePhoto())
															sm = new SendPhoto(chatId, cmd.getImageURL())
																	.parseMode(cmd.getParseMode())
																	.caption(txt);
														else
															sm = new SendPhoto(chatId, cmd.getImageBytes())
																	.parseMode(cmd.getParseMode())
																	.caption(txt);
														if(cmd.hasInlineKeyboard()) sm.replyMarkup(cmd.getInlineKeyboard());
														if(cmd.hasReplyKeyboard())  sm.replyMarkup(cmd.getReplyKeyboard());
														cmd.setResponse(bot.execute(sm));
													} else if(cmd.isFile()){
														SendDocument sm;
														if(cmd.isOnlineFile())
															sm = new SendDocument(chatId, cmd.getFileURL())
																	.parseMode(cmd.getParseMode())
																	.caption(txt);
														else
															sm = new SendDocument(chatId, cmd.getFileBytes())
																	.parseMode(cmd.getParseMode())
																	.caption(txt);
														if(cmd.hasInlineKeyboard()) sm.replyMarkup(cmd.getInlineKeyboard());
														if(cmd.hasReplyKeyboard())  sm.replyMarkup(cmd.getReplyKeyboard());
														cmd.setResponse(bot.execute(sm));
													} else {
														SendMessage sm = new SendMessage(chatId, txt)
																.parseMode(cmd.getParseMode())
																.disableWebPagePreview(cmd.disableLinkPreview);
														if(cmd.hasInlineKeyboard()) sm.replyMarkup(cmd.getInlineKeyboard());
														cmd.setResponse(bot.execute(sm));
													}
												}
												if(type == CALLBACK)
													bot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
											}
										}
									}
							} catch (Exception e){
								e.printStackTrace();
							}
						}
					}.start();
				}
			} catch(Exception e){
				//e.printStackTrace();
			}
			App.wait(500);
		}
	}
	
	public static String getName(Update u, int type){
		String name = "";
		User usr = getFrom(u, type);
		if(usr != null) name = usr.firstName();
		return name;
	}
	public static String getFullname(Update u, int type){
		String name = "";
		User usr = getFrom(u, type);
		if(usr != null) name = usr.firstName() + " " + usr.lastName();
		return name;
	}
	public static String getUsername(Update u, int type){
		String username = "";
		User usr = getFrom(u, type);
		if(usr != null) username = usr.username();
		return username;
	}
	public static long getFromId(Update u, int type){
		long id = -1;
		User usr = getFrom(u, type);
		if(usr != null) id = usr.id();
		return id;
	}
	public static User getFrom(Update u, int type){
		User usr = null;
		switch(type){
			case CALLBACK: {
				usr = u.callbackQuery().from();
				break;
			}
			case MESSAGE: {
				usr = u.message().from();
				break;
			}
		}
		return usr;
	}
	public static long getChatid(Update u, int type){
		Message m = u.message();
		if(type == CALLBACK) m = u.callbackQuery().message();
		return m.chat().id();
	}
	public static String getText(Update u, int type){
		String text = "";
		switch(type){
			case CALLBACK: {
				text = u.callbackQuery().data();
				break;
			}
			case MESSAGE: {
				text = u.message().text();
				break;
			}
		}
		return text;
	}
	public static int getUpdateType(Update u){
		int type = OTHERS;
		if(u != null){
				 if(u.callbackQuery() != null && u.callbackQuery().data() != null) type = CALLBACK;
			else if(u.message() != null && u.message().text() != null) type = MESSAGE;
		}
		return type;
	}
	public int sendMessage(long chatId, String message) {
		SendMessage request = new SendMessage(chatId, message)
	        .parseMode(ParseMode.HTML)
	        .disableWebPagePreview(true)
	        .disableNotification(false);
		SendResponse sendResponse = bot.execute(request);
		if(!sendResponse.isOk()) {
			return -1;
		}
		return sendResponse.message().messageId();
	}
	
	public int getOffset(){
		return offset;
	}
	
	@SuppressWarnings("unused")
	private static void copyrightTM(){
		System.out.println("Command engine made by Stranck (c)"
						 + ""
						 + "Need help? Contact me!"
						 + "Telegram channel: https://t.me/Stranck"
						 + "Telegram user: https://t.me/LucaStranck"
						 + "YouTube channel: https://www.youtube.com/channel/UCmMWUz0QZ7WhIBx-1Dz-IGg"
						 + "Twitter: https://twitter.com/LStranck https://twitter.com/stranckV2"
						 + "Instagram: https://www.instagram.com/stranck/"
						 + "Github: https://github.com/stranck"
						 + "Pastebin: https://pastebin.com/u/Stranck");
	}
}
