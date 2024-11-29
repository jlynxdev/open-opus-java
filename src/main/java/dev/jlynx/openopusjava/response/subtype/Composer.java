package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the composer object in the Open Opus API response body.
 */
public class Composer {

    private final int id;
    private final String lastName;
    private final String fullName;
    private final LocalDate birth;
    private final LocalDate death;
    private final Epoch epoch;
    private final String portraitUri;

    @JsonCreator
    public Composer(
            @JsonProperty("id")
            int id,
            @JsonProperty("name")
            String lastName,
            @JsonProperty("complete_name")
            String fullName,
            @JsonProperty("birth")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate birth,
            @JsonProperty("death")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate death,
            @JsonProperty("epoch")
            Epoch epoch,
            @JsonProperty("portrait")
            String portraitUri
    ) {
        this.id = id;
        this.lastName = lastName;
        this.fullName = fullName;
        this.birth = birth;
        this.death = death;
        this.epoch = epoch;
        this.portraitUri = portraitUri;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public Optional<LocalDate> getDeath() {
        return Optional.ofNullable(death);
    }

    public Epoch getEpoch() {
        return epoch;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composer that = (Composer) o;
        return id == that.id &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(birth, that.birth) &&
                Objects.equals(death, that.death) &&
                epoch == that.epoch &&
                Objects.equals(portraitUri, that.portraitUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, fullName, birth, death, epoch, portraitUri);
    }

    @Override
    public String toString() {
        return "Composer{" +
                "id=" + id +
                ", surname='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", epoch=" + epoch +
                ", portraitUri='" + portraitUri + '\'' +
                '}';
    }
}
