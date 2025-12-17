package com.example.electionsurvey2;

/**
 * Ward Model Class
 * Represents a ward within an area in the election survey system
 */
public class Ward {
    private int id;
    private String wardName;

    /**
     * Constructor
     * @param id Ward ID from database
     * @param wardName Name of the ward
     */
    public Ward(int id, String wardName) {
        this.id = id;
        this.wardName = wardName;
    }

    /**
     * Get ward ID
     * @return Ward ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set ward ID
     * @param id Ward ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get ward name
     * @return Ward name
     */
    public String getWardName() {
        return wardName;
    }

    /**
     * Set ward name
     * @param wardName Ward name
     */
    public void setWardName(String wardName) {
        this.wardName = wardName;
    }
}





