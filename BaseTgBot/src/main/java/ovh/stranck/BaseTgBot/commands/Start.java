package ovh.stranck.BaseTgBot.commands;


import ovh.stranck.BaseTgBot.Command;
import ovh.stranck.BaseTgBot.Settings;
import ovh.stranck.BaseTgBot.Telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public class Start extends Command {
	protected String execCommand(TelegramBot bot, Update update) {
		Command cmd = Command.getCommand("start", Settings.botUser, Telegram.CALLBACK);
		String text = cmd.execute(bot, update);
		this.inlineKeyboard = cmd.getInlineKeyboard();
		this.imageSource = cmd.getImageURL();
		return text;
	}
}
