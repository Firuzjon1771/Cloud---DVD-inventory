package gr.aegean.dvd.service;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;

import gr.aegean.dvd.domain.DVD;
import gr.aegean.dvd.exception.InternalServerErrorException;
import gr.aegean.dvd.utility.DBHandler;
import gr.aegean.dvd.utility.HTMLHandler;
import spark.Request;
import spark.Response;

public class DVDService {
	
	private static boolean jsonContentType(String type) {
		if (type != null && !type.trim().equals("") && type.equals("application/json")) return true;
		else return false;
	}
	
	private static boolean authenticate(Request req) throws InternalServerErrorException{
		boolean authenticated = false;
		String auth = req.headers("Authorization");
		if(auth != null && auth.startsWith("Basic")) {
			String b64Credentials = auth.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(b64Credentials));
			System.out.println(credentials);
			String[] creds = credentials.split(":");
			authenticated = DBHandler.existsUser(creds[0],creds[1]);
		}

		return authenticated;
	}
	
	private static void deleteDVD() {
		delete("/api/dvds/:id", (req, res) -> {		
			try {
				if (!authenticate(req)) {
					res.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
					res.status(401);
					return "Not Authenticated";
				}
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			String id = req.params(":id");
			
			boolean deleted = false;
			
			try {
				deleted = DBHandler.deleteDVD(id);
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			if (deleted) {
				res.status(204);
				return "";
			}
			else {
				res.status(404);
				return "The requested resource was not found";
			}
		});
	}
	
	private static void putDVD() {
		put("/api/dvds/:id", (req, res) -> {
			try {
				if (!authenticate(req)) {
					res.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
					res.status(401);
					return "Not Authenticated";
				}
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			if (!jsonContentType(req.contentType())) {
				res.status(415);
				return "Only accept JSON as content type";
			}
			
			String body = req.body();
			DVD dvd = null;
			if (body == null || body.trim().equals("")) {
				res.status(400);
				return "The request does not have any content";
			}
			else {
				try {
					dvd = new Gson().fromJson(body,DVD.class);
				}
				catch(Exception e) {
					res.status(400);
					return "Did not provide a proper JSON content for the posted DVD";
				}
			}
			
			String dvdId = dvd.getId();
			String id = req.params(":id");
			if (dvdId == null || dvdId.trim().equals("")) {
				res.status(400);
				return "Did not provide an ID in the supplied DVD";
			}
			else if (!dvdId.equals(id)) {
				res.status(400);
				return "The DVD's ID does not match the ID path parameter";
			}
			// Checking all obligatory fields in DVD apart from ID
			String title = dvd.getTitle();
			if (title == null || title.trim().equals("")) {
				res.status(400);
				return "MUST provide the title of the DVD";
			}
			String director = dvd.getDirector();
			if (director == null || director.trim().equals("")) {
				res.status(400);
				return "MUST provide the director of the DVD";
			}
			int duration = dvd.getDuration();
			if (duration <= 0) {
				res.status(400);
				return "MUST provide a positive duration for the DVD";
			}
			
			boolean updated = false;
				
			try {
				updated = DBHandler.updateDVD(dvd);
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			if (!updated) {
				res.status(404);
				return "Requested resource was not found";
			}
			
			res.status(204);
			return "";
		});
	}

	private static void postDVD() {
		post("/api/dvds", (req, res) -> {
			try {
				if (!authenticate(req)) {
					res.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
					res.status(401);
					return "Not Authenticated";
				}
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			if (!jsonContentType(req.contentType())) {
				res.status(415);
				return "Only accept JSON as content type";
			}
			
			String body = req.body();
			DVD dvd = null;
			if (body == null || body.trim().equals("")) {
				res.status(400);
				return "The request does not have any content";
			}
			else {
				try {
					dvd = new Gson().fromJson(body, DVD.class);
				}
				catch(Exception e) {
					res.status(400);
					return "Did not provide a proper JSON content for the posted DVD";
				}
			}
				
			String dvdId = dvd.getId();
			if (dvdId == null || dvdId.trim().equals("")) {
				res.status(400);
				return "Did not provide an ID in the supplied DVD";
			}
				
			// Checking all obligatory fields in DVD apart from ID
			String title = dvd.getTitle();
			if (title == null || title.trim().equals("")) {
				res.status(400);
				return "MUST provide the title of the DVD";
			}
			String director = dvd.getDirector();
			if (director == null || director.trim().equals("")) {
				res.status(400);
				return "MUST provide the director of the DVD";
			}
			int duration = dvd.getDuration();
			if (duration <= 0) {
				res.status(400);
				return "MUST provide a positive duration for the DVD";
			}
				
			boolean exists = false;
			try {
				exists = DBHandler.existsDVD(dvdId);
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
			
			if (exists) {
				res.status(400);
				return "DVD with given ID already exists";
			}
			else {
				try {
					DBHandler.createDVD(dvd);
				}
				catch(Exception e) {
					res.status(500);
					return e.getMessage();
				}
			}
			
			res.status(201);
			return req.url() + '/' + dvd.getId();
		});
	}

	private static void getDVDHTML() {
		get("/api/dvds/:id", "text/html", (req, res) -> {
			return getDVD(req, res, false);
		});
	}
	private static void getDVDJSON() {
		get("/api/dvds/:id", "application/json", (req, res) -> {
			return getDVD(req, res, true);
		});
	}

	private static String getDVD(Request req, Response res, boolean isJSON) {
		String id = req.params(":id");
		DVD dvd = null;
		try {
			dvd = DBHandler.getDVD(id);
		}
		catch(Exception e) {
			res.status(500);
			return e.getMessage();
		}
		
		if (dvd != null) {
			res.status(200);
				
			if (isJSON) {
				res.type("application/json");
				return new Gson().toJson(dvd);
			}
			else {
				String answer = HTMLHandler.createHtmlDVD(dvd);
				res.type("text/html");
				return answer;
			}
		}
		else {
			res.status(404);
			return "Requested resource was not found";
		}	
	}

	private static void getDVDsJSON() {
		get("/api/dvds", "application/json", (req, res) -> {
			return getDVDs(req, res, true);
		});
	}

	private static void getDVDsHTML() {
		get("/api/dvds", "text/html", (req, res) -> {
			return getDVDs(req, res, false);
		});
	}

	private static String getDVDs(Request req, Response res, boolean isJSON) {
		String title = req.queryParams("title");
		String director = req.queryParams("director");
		String[] genresArr = req.queryParamsValues("genres");
		List<String> genres = null;
		if (genresArr != null && genresArr.length > 0) {
			genres = Arrays.asList(genresArr);
		}
		List<DVD> dvds = null;
		if ((title != null && !title.trim().equals("")) || (director != null && !director.trim().equals("")) || (genres != null && !genres.isEmpty())){
			try {
				dvds = DBHandler.getDVDs(title, director, genres);
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
		}
		else {
			try {
				dvds = DBHandler.getAllDVDs();
			}
			catch(Exception e) {
				res.status(500);
				return e.getMessage();
			}
		}
		
		if (isJSON) {
			res.status(200);
			res.type("application/json");
			return new Gson().toJson(dvds);
		}
		else {
			res.status(200);
			res.type("text/html");
			if (dvds == null) {
				dvds = new ArrayList<DVD>();
			}
			String answer = HTMLHandler.createHtmlDVDs(dvds);
			return answer;
		}
	}

	private static void run() {
		port(30000);
		staticFileLocation("public");
		getDVDsJSON();
		getDVDsHTML();
		getDVDHTML();
		getDVDJSON();
		postDVD();
		putDVD();
		deleteDVD();
	}

	public static void main(String[] args) {
		run();
	}
}