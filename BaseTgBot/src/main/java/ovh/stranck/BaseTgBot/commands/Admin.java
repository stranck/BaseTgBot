package ovh.stranck.BaseTgBot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import ovh.stranck.BaseTgBot.Command;
import ovh.stranck.BaseTgBot.Settings;
import ovh.stranck.BaseTgBot.Telegram;

public class Admin extends Command {
	@Override
	protected String execCommand(TelegramBot bot, Update update) {
		Command cmd = Command.getCommand("Admin", Settings.botUser, Telegram.CALLBACK);
		String text = cmd.execute(bot, update);
		super.inlineKeyboard = cmd.getInlineKeyboard();
		return text;
	}
}
