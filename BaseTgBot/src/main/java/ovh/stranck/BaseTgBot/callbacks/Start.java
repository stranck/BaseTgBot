package ovh.stranck.BaseTgBot.callbacks;

import ovh.stranck.BaseTgBot.Command;
import ovh.stranck.BaseTgBot.Settings;
import ovh.stranck.BaseTgBot.Telegram;

import java.util.Random;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;

public class Start extends Command {
	private static final String[] GREETINGS = {"Salve", "Salvissimo", "Buongiornissimo", "Ave", "Ciao", "Buondì", "Ao", "Waju", "Wewe",
			"Buonasera", "Wee", "Oi", "Ué", "Hey", "Heyy", "Heyyy", "Heyyyy", "Heyà", "Oe", "'sera", "Yo", "Howdy", "Heylà",
			"Wei", "Hei", "Cucù", "Hi", "Hello", "Henlo", "Hewwo", "Greetings", "Hey there", "Hi there", "Hello there"};
	private static final Random r = new Random();
	
	protected String execCommand(TelegramBot bot, Update update){
		try {
			if(super.updateType == Telegram.CALLBACK && super.args.length == 0)
				if(!bot.execute(new DeleteMessage(Telegram.getChatid(update, super.updateType), update.callbackQuery().message().messageId())).isOk())
					bot.execute(new DeleteMessage(Telegram.getChatid(update, super.updateType), super.response.message().messageId()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.query.setStep("", 0);
		this.inlineKeyboard = new InlineKeyboardMarkup(new InlineKeyboardButton[][] {
			{
				new InlineKeyboardButton("📰 Channel 📰").url(Settings.channel), 
				new InlineKeyboardButton("👥 Group 👥").url(Settings.group)
			},
			{
				new InlineKeyboardButton("Stranck's channel").url("https://t.me/Stranck"),
				new InlineKeyboardButton("Stranck's telegram").url("https://t.me/LucaStranck"),
			},
			{
				new InlineKeyboardButton("Stranck's youtube").url("https://www.youtube.com/channel/UCmMWUz0QZ7WhIBx-1Dz-IGg"),
				new InlineKeyboardButton("Stranck's twitter").url("https://twitter.com/LStranck")
			},
			{
				new InlineKeyboardButton("Stranck's instagram").url("https://www.instagram.com/stranck"),
				new InlineKeyboardButton("Stranck's pastebin").url("https://pastebin.com/u/Stranck")
			},
		});
     
		return GREETINGS[r.nextInt(GREETINGS.length)] + " " + Command.removeBadChars(Telegram.getFullname(update, updateType)).replaceAll("_", "") + "!" +
			 " *Welcome into " + Settings.botUser + "!*\nA base telegram bot made by @Stranck!\n\n"
			 + "If you're reading this, congrats! You have the [REDACTED]\n"
			 + "Github repo of the bot: https://www.github.com/stranck/BaseTgBot";
	}
}
