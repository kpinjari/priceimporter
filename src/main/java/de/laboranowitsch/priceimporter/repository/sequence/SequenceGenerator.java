package de.laboranowitsch.priceimporter.repository.sequence;

/**
 * SequenceIdGenerator for generating database sequences.
 *
 * @author christian@laboranowitsch.de
 */
public interface SequenceGenerator {

    /**
     * Creates the next sequence for a given sequence name.
     *
     * @param sequenceName
     * @return next id
     */
    Long getNextSequence(String sequenceName);
}
