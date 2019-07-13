package ovh.stranck.BaseTgBot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;

import ovh.stranck.BaseTgBot.Command;

public class Secretcreditcommand extends Command {
	
	@Override
	protected String execCommand(TelegramBot bot, Update update) {
		String text = DefaultCommand.NOTFOUND_TEXT;
		if(super.args.length == 1 && super.args[0].equals("7331")){
			text = "Plz don't tell anybody that this bot was made by @Stranck."
			          + "\n\nP.S.: you lost the game xd lol I II II I_";
			super.parseMode = ParseMode.HTML;
		}
		return text;
	}

}
