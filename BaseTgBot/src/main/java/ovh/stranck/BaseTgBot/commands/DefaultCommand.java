package ovh.stranck.BaseTgBot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.response.SendResponse;

import ovh.stranck.BaseTgBot.App;
import ovh.stranck.BaseTgBot.Command;
import ovh.stranck.BaseTgBot.Settings;
import ovh.stranck.BaseTgBot.Telegram;

public class DefaultCommand extends Command {
	public static final String NOTFOUND_TEXT = "*Command not found*\nTry to restart the bot with /start!";
	
	private Command cmd;
	
	@Override
	protected String execCommand(TelegramBot bot, Update update) {
		String text = "";
		String storedCommand = super.query.readStep();
		if(!storedCommand.equals("")){
			super.query.setStep("", Telegram.OTHERS);
			int type = Integer.parseInt("" + storedCommand.charAt(0));
			storedCommand = storedCommand.substring(1);
			for(String s : super.args)
				storedCommand += " " + s;
			App.LOGGER.finer("Stored command: " + storedCommand);
			cmd = Command.getCommand(storedCommand, Settings.botUser, type);
			text = cmd.execute(bot, update);
			super.inlineKeyboard = cmd.getInlineKeyboard();
		} else {
			text = NOTFOUND_TEXT;
			super.replyKeyboard = new ReplyKeyboardRemove();
		}
		return text;
	}

	@Override
	public void setResponse(SendResponse sr){
		if(cmd != null)
			cmd.setResponse(sr);
	}
}
