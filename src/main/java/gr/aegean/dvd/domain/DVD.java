package gr.aegean.dvd.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DVD { 
    private String id;
    private String title;
    private List<String> actors;
    private String director;
    private String genre;
    private String releaseDate;

    public DVD() {}

    public DVD(String id, String title, List<String> actors, String director, String genre, String releaseDate) {
        this.id = id;
        this.title = title;
        this.actors = actors;
        this.director = director;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public String toString(){
        return "DVD(" + id + ", " + title + ", " + actors + ")";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}