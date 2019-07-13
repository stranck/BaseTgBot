package ovh.stranck.BaseTgBot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
	public static final Logger LOGGER = Logger.getLogger(App.class.getName());
	public static Telegram bot;
	
	public static void main(String[] args) throws Exception {		
	    Handler handler = new ConsoleHandler();
	    handler.setLevel(Level.FINEST);
	    handler.setFormatter(new Format());
	    LOGGER.addHandler(handler);
	    LOGGER.setLevel(Level.FINEST);
	    LOGGER.setUseParentHandlers(false);
	    LOGGER.config("Starting up");
	    FileHandler fh = new FileHandler("log.txt", true);
	    fh.setFormatter(new Format());
	    LOGGER.addHandler(fh);
	    Settings.loadSettings();
	    bot = new Telegram();
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	UsersDB.closeConnection();
	        	LOGGER.config("Exiting...");
	        }
	    });
	}
	
	public static String getThrow(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static void wait(int ms){
		try{
		    Thread.sleep(ms);
		} catch(Exception ex){
		    Thread.currentThread().interrupt();
		}
	}
	
}
