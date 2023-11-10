package gr.aegean.dvd.utility;

import java.util.List;

import gr.aegean.dvd.domain.DVD;

public class HTMLHandler {
	
	public static String getActors(List<String> actors) {
		String str = actors.get(0);
		for (int i = 1; i < actors.size(); i++) {
			str += ", " + actors.get(i);
		}
		return str;
	}
	
	private static String getStringFieldValue(String fieldValue) {
		if (fieldValue == null || fieldValue.trim().equals("")) {
			return "";
		} else {
			return fieldValue;
		}
	}
	
	private static String createDVDRow(DVD dvd) {
		String str = "<tr>";
		str += "<td>" + dvd.getIsbn() + "</td>";
		str += "<td>" + dvd.getTitle() + "</td>";
		str += "<td>" + dvd.getCategory() + "</td>";
		str += "<td>" + getActors(dvd.getActors()) + "</td>";
		str += "<td>" + dvd.getDirector() + "</td>";
		str += "<td>" + getStringFieldValue(dvd.getLanguage()) + "</td>";
		str += "<td>" + getStringFieldValue(dvd.getSummary()) + "</td>";
		str += "<td>" + getStringFieldValue(dvd.getDate()) + "</td>";
		str += "</tr>\n";
		
		return str;
	}
	
	public static String createHtmlDVDs(List<DVD> dvds) {
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>The DVDs from Aegean Library</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		answer += "<h1>DVDS</h1>\n";
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>The requested DVDs</caption>\n";
		answer += "<tr><th>Isbn</th><th>Title</th><th>Category</th><th>Actors</th>";
		answer += "<th>Director</th><th>Language</th><th>Summary</th>";
		answer += "<th>Release Date</th>";
		answer += "</tr>\n";
		if (dvds != null) {
			for (DVD dvd : dvds) {
				answer += createDVDRow(dvd);
			}
		}
		answer += "</table>\n";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
	
	public static String createHtmlDVD(DVD dvd) {
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>A DVD from Aegean Library</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		if (dvd != null && dvd.getIsbn() != null && !dvd.getIsbn().trim().equals("")) {
			answer += "<h1>DVD " + dvd.getIsbn() + "</h1>\n";
		}
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>The requested DVD</caption>\n";
		answer += "<tr><th>Isbn</th><th>Title</th><th>Category</th><th>Actors</th>";
		answer += "<th>Director</th><th>Language</th><th>Summary</th>";
		answer += "<th>Release Date</th>";
		answer += "</tr>\n";
		if (dvd != null && dvd.getIsbn() != null && dvd.getCategory() != null
				&& dvd.getTitle() != null && dvd.getActors() != null && !dvd.getActors().isEmpty()) {
			answer += createDVDRow(dvd);
		}
		answer += "</table>\n";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
}

