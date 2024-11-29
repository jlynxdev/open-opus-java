package dev.jlynx.openopusjava.request;

import dev.jlynx.openopusjava.response.subtype.Epoch;
import dev.jlynx.openopusjava.response.subtype.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Represents a set of criteria for filtering random musical works in the OpenOpus API.
 * <p>
 * The {@code RandomWorksCriteria} class contains optional fields that can be used to filter random works by attributes
 * such as popularity, recommendation status, genre, epoch, and specific composers or works to include or exclude.
 * The criteria can be constructed using the {@link RandomWorksCriteriaBuilder} to allow flexibility in specifying
 * only the desired filters. That is the recommended approach of creating instances of this class.
 * </p>
 *
 * <p>Available criteria include:</p>
 * <ul>
 *   <li>{@code popularWork} - Filter by popular works (true or false).</li>
 *   <li>{@code recommendedWork} - Filter by recommended works (true or false).</li>
 *   <li>{@code popularComposer} - Filter by popular composers (true or false).</li>
 *   <li>{@code recommendedComposer} - Filter by recommended composers (true or false).</li>
 *   <li>{@code genre} - Filter by a specific genre, represented by a {@code Genre} object.</li>
 *   <li>{@code epoch} - Filter by a specific epoch, represented by an {@code Epoch} object.</li>
 *   <li>{@code composer} - A list of composer IDs to include in the search.</li>
 *   <li>{@code composerNot} - A list of composer IDs to exclude from the search.</li>
 *   <li>{@code work} - A list of work IDs to include in the search.</li>
 * </ul>
 *
 * Example usage:
 * <pre>{@code
 * RandomWorksCriteria criteria = RandomWorksCriteria.builder()
 *     .setPopularWork(true)
 *     .setGenre(Genre.CLASSICAL)
 *     .build();
 * }</pre>
 *
 * @see RandomWorksCriteriaBuilder
 */
public class RandomWorksCriteria {

    private final Boolean popularWork;
    private final Boolean recommendedWork;
    private final Boolean popularComposer;
    private final Boolean recommendedComposer;
    private final Genre genre;
    private final Epoch epoch;
    private final List<Integer> composer;
    private final List<Integer> composerNot;
    private final List<Integer> work;

    public RandomWorksCriteria(
            Boolean popularWork,
            Boolean recommendedWork,
            Boolean popularComposer,
            Boolean recommendedComposer,
            Genre genre,
            Epoch epoch,
            List<Integer> composer,
            List<Integer> composerNot,
            List<Integer> work
    ) {
        this.composerNot = composerNot;
        this.composer = composer;
        this.popularWork = popularWork;
        this.recommendedWork = recommendedWork;
        this.popularComposer = popularComposer;
        this.recommendedComposer = recommendedComposer;
        this.genre = genre;
        this.epoch = epoch;
        this.work = work;
    }

    public Optional<Boolean> getPopularWork() {
        return Optional.ofNullable(popularWork);
    }

    public Optional<Boolean> getRecommendedWork() {
        return Optional.ofNullable(recommendedWork);
    }

    public Optional<Boolean> getPopularComposer() {
        return Optional.ofNullable(popularComposer);
    }

    public Optional<Boolean> getRecommendedComposer() {
        return Optional.ofNullable(recommendedComposer);
    }

    public Optional<Genre> getGenre() {
        return Optional.ofNullable(genre);
    }

    public Optional<Epoch> getEpoch() {
        return Optional.ofNullable(epoch);
    }

    public Optional<List<Integer>> getComposer() {
        return Optional.ofNullable(composer);
    }

    public Optional<List<Integer>> getComposerNot() {
        return Optional.ofNullable(composerNot);
    }

    public Optional<List<Integer>> getWork() {
        return Optional.ofNullable(work);
    }

    public static RandomWorksCriteriaBuilder builder() {
        return new RandomWorksCriteriaBuilder();
    }


    /**
     * Builder class for constructing instances of {@code RandomWorksCriteria}.
     * <p>
     * The {@code RandomWorksCriteriaBuilder} provides a fluent API to set optional filtering criteria,
     * creating a new {@code RandomWorksCriteria} instance with the specified filters.
     * </p>
     */
    public static class RandomWorksCriteriaBuilder {
        private Boolean popularWork;
        private Boolean recommendedWork;
        private Boolean popularComposer;
        private Boolean recommendedComposer;
        private Genre genre;
        private Epoch epoch;
        private List<Integer> composer;
        private List<Integer> composerNot;
        private List<Integer> work;

        private RandomWorksCriteriaBuilder() { }

        /**
         * Sets the popular work filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param popularWork a {@code Boolean} indicating whether to filter by popular works.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setPopularWork(Boolean popularWork) {
            this.popularWork = popularWork;
            return this;
        }

        /**
         * Sets the recommended work filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param recommendedWork a {@code Boolean} indicating whether to filter by recommended works.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setRecommendedWork(Boolean recommendedWork) {
            this.recommendedWork = recommendedWork;
            return this;
        }

        /**
         * Sets the popular composer filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param popularComposer a {@code Boolean} indicating whether to filter by popular composers.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setPopularComposer(Boolean popularComposer) {
            this.popularComposer = popularComposer;
            return this;
        }

        /**
         * Sets the recommended composer filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param recommendedComposer a {@code Boolean} indicating whether to filter by recommended composers.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setRecommendedComposer(Boolean recommendedComposer) {
            this.recommendedComposer = recommendedComposer;
            return this;
        }

        /**
         * Sets the work's genre filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param genre a {@code Boolean} indicating whether to filter by genre.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setGenre(Genre genre) {
            this.genre = genre;
            return this;
        }

        /**
         * Sets the work's epoch filter. If {@code null} is passed, this filter will be omitted.
         *
         * @param epoch a {@code Boolean} indicating whether to filter by musical epoch.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setEpoch(Epoch epoch) {
            this.epoch = epoch;
            return this;
        }

        /**
         * Sets the list of composer IDs to include in the search.
         *
         * @param composer a {@code List} of numbers representing the IDs of composers to include.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setComposer(List<Integer> composer) {
            this.composer = composer;
            return this;
        }

        /**
         * Sets the list of composer IDs to exclude from the search.
         *
         * @param composerNot a {@code List} of numbers representing the IDs of composers to exclude.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setComposerNot(List<Integer> composerNot) {
            this.composerNot = composerNot;
            return this;
        }

        /**
         * Sets the list of work IDs to include in the search.
         *
         * @param workIds a {@code List} of numbers representing the IDs of works to include.
         * @return the current {@code RandomWorksCriteriaBuilder} instance.
         */
        public RandomWorksCriteriaBuilder setWork(List<Integer> workIds) {
            this.work = workIds;
            return this;
        }

        /**
         * Builds and returns a new {@code RandomWorksCriteria} instance with the specified filters.
         *
         * @return a new {@code RandomWorksCriteria} instance.
         */
        public RandomWorksCriteria build() {
            return new RandomWorksCriteria(popularWork, recommendedWork, popularComposer, recommendedComposer, genre, epoch, composer, composerNot, work);
        }
    }
}
