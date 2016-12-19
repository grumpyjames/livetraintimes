package net.digihippo.ltt;

/**
 * Represents a valid station in the National Rail network
 */
public interface Station {
    /**
     * The three letter code of this station, e.g BEE, ELY, or STP
     * (Beeston, Ely, and London St Pancras, respectively)
     * @return the three letter code
     */
    public String threeLetterCode();

    /**
     * The full text name of this station
     * @return the full name
     */
    public String fullName();
}
