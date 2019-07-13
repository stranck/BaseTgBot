package ovh.stranck.BaseTgBot;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UsersDB {
	public static Connection c = null;

	public static void closeConnection() {
		try {
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getConn() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:file:users.db", "SA", "");
		} catch (Exception e) {
			App.LOGGER.warning("Unable to init DataBase. Fatal Error.");
		}
	}

	public static void createUsersTable() {
		try {
			Class.forName("org.sqlite.JDBC");
			DatabaseMetaData dbm = c.getMetaData();
			Statement stmt = c.createStatement();
			ResultSet rs = dbm.getTables(null, null, "users", null);
			if (!rs.next()) {
				stmt.execute("CREATE TABLE \"users\" (id BIGINT PRIMARY KEY, ban BIT, step VARCHAR(64));");
				stmt.execute("CREATE UNIQUE INDEX user_index ON users (id);");
			}
			stmt.close();
			rs.close();
		} catch (Exception e) {
			App.LOGGER.warning("FATAL ERROR WHILE CREATING TABLE: " + e);
		}
	}

	public static void createUser(long userId) {
		if (!checkUserRow(userId)) {
			try {
				PreparedStatement p = c.prepareStatement("INSERT INTO \"users\" (id, ban, step) VALUES (?, 0, '')");
				p.setLong(1, userId);
				p.execute();
				p.close();
			} catch (Exception e) {
				App.LOGGER.warning("Error Registering User " + userId + ": " + e);
			}
		}
	}

	public static <T> void updateUser(long userId, String field, T value) {
		try {
			PreparedStatement p = c.prepareStatement("UPDATE \"users\" SET " + field + "=? WHERE id=?");
			if ((value instanceof String)) {
				p.setString(1, (String) value);
			} else if ((value instanceof Integer)) {
				p.setInt(1, ((Integer) value).intValue());
			} else if ((value instanceof Long)) {
				p.setLong(1, ((Long) value).longValue());
			} else if ((value instanceof Boolean)) {
				p.setBoolean(1, ((Boolean) value).booleanValue());
			} else if ((value instanceof Double)) {
				p.setDouble(1, ((Double) value).doubleValue());
			}
			p.setLong(2, userId);
			p.execute();
			p.close();
		} catch (Exception e) {
			App.LOGGER.warning("Failed to update User: " + e);
		}
	}

	public static boolean readBan(long userId) {
		try {
			PreparedStatement p = c.prepareStatement("SELECT ban FROM \"users\" WHERE id=?");
			p.setLong(1, userId);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				boolean res = rs.getBoolean("ban");
				p.close();
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void setStep(long userId, String step, int type) {
		if (type != 0)
			step = type + step;
		updateUser(userId, "step", step);
	}
	public static void setBan(long userId, boolean ban) {
		updateUser(userId, "ban", ban ? 1 : 0);
	}

	public static boolean checkUserRow(long userId) {
		try {
			PreparedStatement p = c.prepareStatement("SELECT COUNT(*) AS rowcount FROM \"users\" WHERE id=?");
			p.setLong(1, userId);
			ResultSet rs = p.executeQuery();
			int res = rs.getInt("rowcount");
			if (res > 0) {
				p.close();
				return true;
			}
			p.close();
			return false;
		} catch (Exception localException) {
		}
		return false;
	}

	public static Query getQuery(long userId) {
		Query q = null;
		try {
			PreparedStatement p = c.prepareStatement("SELECT * FROM \"users\" WHERE id=?");
			p.setLong(1, userId);
			ResultSet rs = p.executeQuery();
			if (rs.next())
				q = new Query(rs, userId);
			else {
				createUser(userId);
				q = getQuery(userId);
			}
			p.close();
		} catch (Exception e) {
			App.LOGGER.warning("Error Reading DataBase: " + e);
		}
		return q;
	}

	public static ArrayList<String> getIds() {
		ArrayList<String> users_ids = new ArrayList<String>();
		try {
			PreparedStatement p;
			p = c.prepareStatement("SELECT id FROM \"users\"");
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				users_ids.add(rs.getString(1));
			}
			p.close();
		} catch (SQLException e) {}
		return users_ids;
	}

	public static String readStep(long userId) {
		try {
			PreparedStatement p = c.prepareStatement("SELECT step FROM \"users\" WHERE id=?");
			p.setLong(1, userId);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				String res = rs.getString("step");
				p.close();
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String toStringUser(long userId) {
		return "*ID:* " + userId + "\n*Status ban:* " + readBan(userId) + "\n*STEP:* " + readStep(userId);
	}
}
