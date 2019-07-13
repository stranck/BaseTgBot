package ovh.stranck.BaseTgBot;

import java.sql.ResultSet;

public class Query {
	private long userId;
	private boolean ban;
	private String step;
	
	public Query(ResultSet rs, long id) throws Exception{
		userId = id;
		ban = rs.getBoolean("ban");
		step = rs.getString("step");
	}
	
	public long getId(){
		return userId;
	}
	
	public boolean readBan(){
		return ban;
	}
	public String readStep(){
		return step;
	}
	
	public void setStep(String step, int type){
		if(type != 0) {
			step = type + step;
		}
		if(this.step != step){
			this.step = step;
			UsersDB.updateUser(userId, "step", step);
		}
	}
	public void setBan(boolean ban){
		if(this.ban != ban){
			this.ban = ban;
			UsersDB.updateUser(userId, "ban", ban ? 1 : 0);
		}
	}
}
