package com.example.electionsurvey2;

/**
 * Area Model Class
 * Represents an area/location in the election survey system
 */
public class Area {
    private int id;
    private String areaName;

    /**
     * Constructor
     * @param id Area ID from database
     * @param areaName Name of the area
     */
    public Area(int id, String areaName) {
        this.id = id;
        this.areaName = areaName;
    }

    /**
     * Get area ID
     * @return Area ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set area ID
     * @param id Area ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get area name
     * @return Area name
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * Set area name
     * @param areaName Area name
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}





