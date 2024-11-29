package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents the work object in the Open Opus API response body.
 */
public class Work {

    private final int id;
    private final String title;
    private final String subtitle;
    private final String searchTerms;
    private final boolean popular;
    private final boolean recommended;
    private final Genre genre;

    @JsonCreator
    public Work(
            @JsonProperty("id")
            int id,
            @JsonProperty("title")
            String title,
            @JsonProperty("subtitle")
            String subtitle,
            @JsonProperty("searchterms")
            String searchTerms,
            @JsonProperty("popular")
            String popular,
            @JsonProperty("recommended")
            String recommended,
            @JsonProperty("genre")
            Genre genre
    ) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.searchTerms = searchTerms;
        this.popular = Objects.equals(popular, "1");
        this.recommended = Objects.equals(recommended, "1");
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public boolean isPopular() {
        return popular;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public Genre getGenre() {
        return genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Work work = (Work) o;
        return id == work.id &&
                popular == work.popular &&
                recommended == work.recommended &&
                Objects.equals(title, work.title) &&
                Objects.equals(subtitle, work.subtitle) &&
                Objects.equals(searchTerms, work.searchTerms) &&
                genre == work.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, subtitle, searchTerms, popular, recommended, genre);
    }

    @Override
    public String toString() {
        return "Work{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", searchTerms='" + searchTerms + '\'' +
                ", popular=" + popular +
                ", recommended=" + recommended +
                ", genre=" + genre +
                '}';
    }
}
