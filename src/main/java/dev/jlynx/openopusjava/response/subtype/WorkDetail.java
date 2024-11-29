package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WorkDetail {

    private final int id;
    private final String title;
    private final String subtitle;
    private final Genre genre;
    private final String searchMode;
    private final List<String> searchTerms;

    private final String catalogue;
    private final String catalogueNumber;

    @JsonCreator
    public WorkDetail(
            @JsonProperty("id")
            int id,
            @JsonProperty("title")
            String title,
            @JsonProperty("subtitle")
            String subtitle,
            @JsonProperty("genre")
            Genre genre,
            @JsonProperty("searchmode")
            String searchMode,
            @JsonProperty("searchterms")
            List<String> searchTerms,
            @JsonProperty("catalogue")
            String catalogue,
            @JsonProperty("catalogue_number")
            String catalogueNumber) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.genre = genre;
        this.searchMode = searchMode;
        this.searchTerms = searchTerms;
        this.catalogue = catalogue;
        this.catalogueNumber = catalogueNumber;
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

    public Genre getGenre() {
        return genre;
    }

    public String getSearchMode() {
        return searchMode;
    }

    public List<String> getSearchTerms() {
        return searchTerms;
    }

    public Optional<String> getCatalogue() {
        return Optional.ofNullable(catalogue);
    }

    public Optional<String> getCatalogueNumber() {
        return Optional.ofNullable(catalogueNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkDetail that = (WorkDetail) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(subtitle, that.subtitle) &&
                genre == that.genre &&
                Objects.equals(searchMode, that.searchMode) &&
                Objects.equals(searchTerms, that.searchTerms) &&
                Objects.equals(catalogue, that.catalogue) &&
                Objects.equals(catalogueNumber, that.catalogueNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, subtitle, genre, searchMode, searchTerms, catalogue, catalogueNumber);
    }

    @Override
    public String toString() {
        return "WorkDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", genre=" + genre +
                ", searchMode='" + searchMode + '\'' +
                ", searchTerms=" + searchTerms +
                ", catalogue='" + catalogue + '\'' +
                ", catalogueNumber='" + catalogueNumber + '\'' +
                '}';
    }
}
