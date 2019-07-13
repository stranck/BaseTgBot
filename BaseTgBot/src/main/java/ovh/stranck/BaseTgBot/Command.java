package ovh.stranck.BaseTgBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.response.SendResponse;

import ovh.stranck.BaseTgBot.commands.DefaultCommand;

public abstract class Command {
	private static final String COMMANDS_PACKAGE = "ovh.stranck.BaseTgBot.commands.";
	private static final String CALLBACK_PACKAGE = "ovh.stranck.BaseTgBot.callbacks.";

	protected Query query;
	protected boolean disableLinkPreview = false;
	protected int updateType;
	protected SendResponse response;
	protected String[] args;
	protected InlineKeyboardMarkup inlineKeyboard = null;
	protected String imageSource = null;
	protected byte[] imageBytes = null;
	protected String fileSource = null;
	protected byte[] fileBytes = null;
	protected Keyboard replyKeyboard = null;
	protected ParseMode parseMode = ParseMode.Markdown;
	protected abstract String execCommand(TelegramBot bot, Update update);
	public final String execute(TelegramBot bot, Update update){
		updateType = Telegram.getUpdateType(update);
		String s = "You've been banned from using the bot. If you think it's an error, join our group";
		long userId = Telegram.getFromId(update, updateType);
		query = UsersDB.getQuery(userId);
		boolean ban = query.readBan();
		if(ban && !Settings.isAdmin(userId)) {
			inlineKeyboard = new InlineKeyboardMarkup(new InlineKeyboardButton[][] {{
				new InlineKeyboardButton("👥 Join the group! 👥").url(Settings.group)
			}});
		} else {
			s = execCommand(bot, update);
		}
		return s;
	}
	
	public void setResponse(SendResponse sr){
		response = sr;
	}
	
	public boolean isFile(){
		return fileSource != null || fileBytes != null;
	}
	public boolean isOnlineFile(){
		return fileSource != null;
	}
	public boolean isPhoto(){
		return imageSource != null || imageBytes != null;
	}
	public boolean isOnlinePhoto(){
		return imageSource != null;
	}
	public boolean hasInlineKeyboard(){
		return inlineKeyboard != null;
	}
	public boolean hasReplyKeyboard(){
		return replyKeyboard != null;
	}
	public boolean isLinkPreviewDisabled(){
		return disableLinkPreview;
	}
	
	public InlineKeyboardMarkup getInlineKeyboard(){
		return inlineKeyboard;
	}
	public Keyboard getReplyKeyboard(){
		return replyKeyboard;
	}
	public String getFileURL(){
		return fileSource;

	}
	public byte[] getFileBytes(){
		return fileBytes;
	}
	public String getImageURL(){
		return imageSource;
	}
	public byte[] getImageBytes(){
		return imageBytes;
	}
	public ParseMode getParseMode(){
		return parseMode;
	}
	
	public static Command getCommand(String text, String userName, int updateType){
		return getCommand(text, userName, updateType, false);
	}
	public static Command getCommand(String text, String userName, int updateType, boolean checkStoredCommand){
		Command c = null;
		if(isThisBot(text, userName)){
			text = removeBadChars(text);
			String originalArgs[] = text.split("\\s+");
			String command = getText(originalArgs[0]);
			if(command.equalsIgnoreCase("start") && originalArgs.length > 1) {
				if(originalArgs[1].charAt(0) == '-'){
					originalArgs[1] = originalArgs[1].substring(1);
				} else {
					command = getText(originalArgs[1].split("_")[0]);
					originalArgs = text.split("_");
				}
			}
			c = getInternalCommand(command, updateType, checkStoredCommand);
			if(c != null) {
				int n = 0;
				if(c instanceof DefaultCommand) n++;
				String newArgs[] = new String[n + originalArgs.length - 1];
				for(int i = (1 - n); i < originalArgs.length; i++)
					newArgs[i - (1 - n)] = originalArgs[i];

				c.args = newArgs;
			}
		}
		return c;
	}
	
	private static Command getInternalCommand(String command, int updateType, boolean checkStoredCommand){
		Command c = null;
		if(checkStoredCommand) c = new DefaultCommand();
		try {
			String packageName = "";
			if(updateType == Telegram.CALLBACK)
				packageName = CALLBACK_PACKAGE;
			else
				packageName = COMMANDS_PACKAGE;
			c = (Command) Class.forName(packageName + command).newInstance();
		} catch (Exception e){}
		return c;
	}
	
	private static String getText(String arg){
		arg = arg.split("@")[0].replaceAll("[^A-Za-z0-9]", "");
		if(arg.length() > 1)
			arg = arg.substring(0, 1).toUpperCase() + arg.substring(1).toLowerCase();
		return arg;
	}
	
	private static boolean isThisBot(String text, String userName){
		String[] sp = text.split("@");
		return !text.contains("@") || sp[sp.length - 1].equalsIgnoreCase(userName);
	}
	
	public static String removeBadChars(String s){
		return s.replaceAll("[^a-zA-Z0-9_\\s+\\-@\\.,/]", "");
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
