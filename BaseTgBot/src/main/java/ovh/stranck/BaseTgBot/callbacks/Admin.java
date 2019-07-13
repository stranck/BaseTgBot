package ovh.stranck.BaseTgBot.callbacks;

import java.util.ArrayList;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import ovh.stranck.BaseTgBot.Command;
import ovh.stranck.BaseTgBot.Settings;
import ovh.stranck.BaseTgBot.Telegram;
import ovh.stranck.BaseTgBot.UsersDB;

public class Admin extends Command {
	
	@Override
	protected String execCommand(TelegramBot bot, Update update) {
		String text = "You're not an admin!";
		long userId = Telegram.getFromId(update, super.updateType);
		
		if(Settings.isAdmin(userId)){
			if(super.updateType == Telegram.CALLBACK)
				if(!bot.execute(new DeleteMessage(Telegram.getChatid(update, super.updateType), update.callbackQuery().message().messageId())).isOk())
					bot.execute(new DeleteMessage(Telegram.getChatid(update, super.updateType), super.response.message().messageId()));
			if(super.args.length > 0){
				switch(super.args[0]){
					case "b" : {
						text = ban(userId, update, bot);
						break;
					}
					case "c" : {
						text = credit(update, bot, userId);
						break;
					}
					case "d" : {
						text = maintenance(update, bot, userId);
						break;
					}
					case "mm" : {
						text = massMex(update, bot);
						break;
					}
				}
			} else {
				text = firstScreen(userId);
			}
		}
		return text;
	}

	private String firstScreen(long userId){
		super.query.setStep("", Telegram.OTHERS);
		super.inlineKeyboard = new InlineKeyboardMarkup(
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("Ban user").callbackData("admin b"),
						new InlineKeyboardButton("Edit user records").callbackData("admin c")
				},
				
				new InlineKeyboardButton[]{
						new InlineKeyboardButton(
								(Settings.maintenance ? "Disable" : "Enable")
									+ " maintenance").callbackData("admin d")
				},
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("Global text").callbackData("admin mm")
				}
			);
        
		return  "*Super secret admin panel*\n\nDon't tell anyone about that!";
	}
	
	private String maintenance(Update update, TelegramBot bot, long userId){
		Settings.maintenance = !Settings.maintenance;
		Settings.saveDynamicSettings();
		bot.execute(new AnswerCallbackQuery(update.callbackQuery().id())
				.text((Settings.maintenance ? "Disable" : "Enable") + " maintenance"));
		return firstScreen(userId);
	}
	
	
	private boolean modifyRecord(Update update, TelegramBot bot, String parameter, long userId, String newValue){
		boolean success = false;
		try {
			switch(parameter){
				case "STEP" : {
					UsersDB.setStep(userId, newValue, Telegram.CALLBACK);
					success = true;
					break;
				}
			}
		} catch (Exception e){}
		return success;
	}	
	
	private String massMex(Update update, TelegramBot bot){
		String text = "Fixture MYDEPRESSION at full; please";
		if(super.args.length == 1){
			text = "Send me the broadcast message";
			addBack("admin");
			super.query.setStep("admin mm", Telegram.CALLBACK);
		} else {
			try {
				String message = update.message().text();
			    ArrayList<String> s = UsersDB.getIds();
					for(String str:s){
						 bot.execute(new SendMessage(str, message)
								 .parseMode(ParseMode.Markdown)
								 .disableWebPagePreview(true));
			    }
				text = "*Message sent to everyone*";
			} catch (Error e){
				text = "Error";
			}
			addBack("admin");
		}
		return text;
	}
	
	private String credit(Update update, TelegramBot bot, long userId){
		String text = "wefiasfjijsiadjioasjioajdijdioajdsiojasdiojsdiosjsdiosjijisjios";
		if(super.args.length == 1){
			text = "Send me the userId";
			addBack("admin");
			super.query.setStep("admin c", Telegram.CALLBACK);
		} else {
			try {
				long id = Long.parseLong(super.args[1]);
				if(UsersDB.checkUserRow(id)){
					switch(super.args.length){
						case 2: {
							text = creditMenu(id, userId);
							break;
						}
						case 3: {
							text = "Send me the new value";
							addBack("admin c " + id);
							super.query.setStep("admin c " + id + " " + super.args[2], Telegram.CALLBACK);
							break;
						}
						case 4: {
							String newValue = super.args[3];
							String parameter = super.args[2];
							if(modifyRecord(update, bot, parameter, id, newValue)){
								text = "`" +  parameter + "` updated to `" + newValue + "` for `" + id + "`";
							} else {
								text = "Can't update `" +  parameter + "` to `" + newValue + "` for `" + id + "`";
							}
							text += "!\n\n" + creditMenu(id, userId);
							break;
						}
					}
				} else text = invalidUserId("admin b", "admin b " + super.args[1], userId);
			} catch (Exception e){
				text = invalidUserId("admin b", "admin b " + super.args[1], userId);
			}
		}
		return text;
	}
	
	private String creditMenu(long id, long userId){
		super.query.setStep("", Telegram.OTHERS);
		super.inlineKeyboard = new InlineKeyboardMarkup(
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("Step").callbackData("admin c " + id + " STEP")
				},
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("🔄 Refresh 🔄").callbackData("admin c " + id),
						new InlineKeyboardButton("↪️ Back").callbackData("admin c")
				}
			);
		return UsersDB.toStringUser(id) + "\n\nSelect the record you wanna change";
	}
	
	
	private String ban(long userId, Update update, TelegramBot bot){
		String text = "y u r still hacking me qwq";
		if(super.args.length == 1){
			text = banMenu(userId);
		} else {
			if(super.args.length == 2){
				text = "Send the userId that you want to " + 
						(super.args[1].equals("b") ? "ban" : "unban") + ".";
				addBack("admin b");
				super.query.setStep("admin b " + super.args[1], Telegram.CALLBACK);
			} else {
				try{
					long id = Long.parseLong(super.args[2]);
					if(UsersDB.checkUserRow(id)){
						if(super.args[1].equals("b")){
							UsersDB.setBan(id, true);
							text = id + " banned.";
							bot.execute(new SendMessage(id, " 🚫 *YOU'VE BEEN BANNED!* 🚫").parseMode(ParseMode.Markdown));
						} else {
							UsersDB.setBan(id,false);
							text = id + " unbanned.";
							bot.execute(new SendMessage(id, " 😇 *YOU'VE BEEN UNBANNED* 😇").parseMode(ParseMode.Markdown));
						}
						text += "\n\n" + banMenu(userId);
					} else text = invalidUserId("admin b", "admin b " + super.args[1], userId);
				} catch (Exception e){
					text = invalidUserId("admin b", "admin b " + super.args[1], userId);
				}
			}
		}
		return text;
	}
	private String banMenu(long userId){
		super.query.setStep("", Telegram.OTHERS);
		super.inlineKeyboard = new InlineKeyboardMarkup(
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("Ban").callbackData("admin b b"),
						new InlineKeyboardButton("Unban").callbackData("admin b u")
				},
				new InlineKeyboardButton[]{
						new InlineKeyboardButton("↪️ Back").callbackData("admin")
				}
			);
		return "Ban menù";
	}
	
	private String invalidUserId(String back, String step, long userId){
		addBack(back);
		super.query.setStep(step, Telegram.CALLBACK);
		return "⚠️ Warning⚠️ \n\nInvalid userId";
	}
	
	private void addBack(String data){
		super.inlineKeyboard = new InlineKeyboardMarkup(
			new InlineKeyboardButton[]{
				new InlineKeyboardButton("↪️ Back").callbackData(data)
			}
		);
	}
}

