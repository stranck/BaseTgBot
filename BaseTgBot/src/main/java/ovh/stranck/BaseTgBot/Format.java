package ovh.stranck.BaseTgBot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Format extends Formatter{
    public static final SimpleDateFormat DATE_FULL = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
    public static final SimpleDateFormat DATE_SMOL = new SimpleDateFormat("ddMM-HHmm");

	
	public Format() {
    	super();
    }

    @Override 
    public String format(final LogRecord record){
    	return "[" + time(DATE_FULL) + "][" + record.getLevel() + "]\t" + record.getMessage() + "\n";
    }
    
	public static String time(SimpleDateFormat sdf) {
		return sdf.format(new Date());	
	}
}