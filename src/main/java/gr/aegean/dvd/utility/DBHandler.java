package gr.aegean.dvd.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import gr.aegean.dvd.configuration.PropertyReader;
import gr.aegean.dvd.domain.DVD;

public final class DBHandler {
	
	private DBHandler() {}
	
	private static Connection getConnection() throws InternalServerErrorException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(
				"jdbc:mysql://" + PropertyReader.getDbHost() + ":" + PropertyReader.getDbPort() + "/dvds",
				PropertyReader.getLogin(),
				PropertyReader.getPwd()
			);
			return con;
		} catch (Exception e) {
			throw new InternalServerErrorException("Cannot connect to the underlying database");
		}
	}
	
	private static DVD getDVDFromRS(ResultSet rs) throws SQLException {
		DVD dvd = new DVD();
		dvd.setIsbn(rs.getString("isbn"));
		dvd.setTitle(rs.getString("title"));
		dvd.setCategory(rs.getString("category"));
		dvd.setActors(getActors(rs.getString("actors")));
		dvd.setDirector(rs.getString("director"));
		dvd.setLanguage(rs.getString("language"));
		dvd.setSummary(rs.getString("summary"));
		dvd.setDate(rs.getString("date"));
		return dvd;
	}
	
	private static List<String> getActors(String str) {
		List<String> actors = new ArrayList<String>();
		String[] res = str.split(", ");
		for (String s : res) {
			actors.add(s);
		}
		return actors;
	}
	
	public static List<DVD> getAllDVDs() throws InternalServerErrorException {
		List<DVD> dvds = null;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM dvd");
			if (rs.next()) {
				dvds = new ArrayList<DVD>();
				dvds.add(getDVDFromRS(rs));
				while (rs.next()) {
					dvds.add(getDVDFromRS(rs));
				}
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return dvds;
	}
	
	public static DVD getDVD(String isbn) throws InternalServerErrorException {
		DVD dvd = null;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "SELECT * FROM dvd WHERE isbn='" + isbn + "'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				dvd = getDVDFromRS(rs);
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return dvd;
	}
	
	public static List<DVD> getDVDs(String title, List<String> actors, String director) throws InternalServerErrorException {
		List<DVD> dvds = null;
		boolean hasTitle = false, hasActors = false, hasDirector = false;
		hasTitle = (title != null && !title.trim().equals(""));
		hasDirector = (director != null && !director.trim().equals(""));
		hasActors = (actors != null && !actors.isEmpty());
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
						String query = "SELECT * FROM dvd";
			if (hasTitle || hasActors || hasDirector) {
				query += " WHERE ";
			}
			if (hasTitle) {
				query += "title = '" + title + "' ";
			}
			if (hasActors) {
				if (hasTitle) {
					query += "AND (";
				} else {
					query += "(";
				}
				query += "actors LIKE '%" + actors.get(0) + "%'";
				for (int i = 1; i < actors.size(); i++) {
					query += " OR actors LIKE '%" + actors.get(i) + "%'";
				}
				query += ")";
			}
			if (hasDirector) {
				if (hasTitle || hasActors) {
					query += "AND ";
				}
				query += "director = '" + director + "'";
			}
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				dvds = new ArrayList<DVD>();
				dvds.add(getDVDFromRS(rs));
				while (rs.next()) {
					dvds.add(getDVDFromRS(rs));
				}
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return dvds;
	}
	
	public static boolean existsDVD(String isbn) throws InternalServerErrorException {
		boolean hasDVD = false;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "SELECT * FROM dvd WHERE isbn='" + isbn + "'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				hasDVD = true;
				}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return hasDVD;
	}
	
	public static boolean updateDVD(DVD dvd) throws InternalServerErrorException {
		Connection con = getConnection();
		boolean updated = false;
		try {
			Statement stmt = con.createStatement();
			String query = "UPDATE dvd SET " +
				"title='" + dvd.getTitle() + "', " +
				"category='" + dvd.getCategory() + "', " +
				"actors='" + getActorsString(dvd.getActors()) + "', " +
				"director='" + dvd.getDirector() + "', " +
				"language='" + dvd.getLanguage() + "', " +
				"summary=" + getFieldValue(dvd.getSummary()) + ", " +
				"date=" + getFieldValue(dvd.getDate()) + " " +
				"WHERE isbn='" + dvd.getIsbn() + "'";
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) {
				updated = true;
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return updated;
	}
	
	private static String getActorsString(List<String> actors) {
		StringBuilder sb = new StringBuilder();
		for (String actor : actors) {
			sb.append(actor);
			sb.append(", ");
		}
		String actorsString = sb.toString();
		actorsString = actorsString.substring(0, actorsString.length() - 2); // Remove the last comma and space
		return actorsString;
	}
	
	private static String getFieldValue(String value) {
		if (value == null 			|| value.trim().equals("")) {
			return new String("NULL");
		} else {
			return "'" + value + "'";
		}
	}
	
	public static boolean createDVD(DVD dvd) throws InternalServerErrorException {
		Connection con = getConnection();
		boolean created = false;
		try {
			Statement stmt = con.createStatement();
			String query = "INSERT INTO dvd (isbn, title, category, actors, director, language, summary, date) " +
				"VALUES ('" + dvd.getIsbn() + "', '" + dvd.getTitle() + "', '" + dvd.getCategory() + "', '" +
				getActorsString(dvd.getActors()) + "', '" + dvd.getDirector() + "', '" + dvd.getLanguage() + "', " +
				getFieldValue(dvd.getSummary()) + ", " + getFieldValue(dvd.getDate()) + ")";
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) {
				created = true;
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return created;
	}
	
	public static boolean deleteDVD(String isbn) throws InternalServerErrorException {
		boolean deleted = false;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "DELETE FROM dvd WHERE isbn='" + isbn + "'";
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) {
				deleted = true;
			}
			con.close();
		} catch (Exception e) {
			throw new InternalServerErrorException("An internal error prevented from getting the information of the DVD inventory");
		}
		return deleted;
	}
}


