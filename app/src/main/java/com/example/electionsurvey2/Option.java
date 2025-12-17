package com.example.electionsurvey2;

/**
 * Option Model Class
 * Represents an option for single/multiple choice questions
 */
public class Option {
    private int id;
    private String optionText;

    /**
     * Constructor
     * @param id Option ID
     * @param optionText Option text
     */
    public Option(int id, String optionText) {
        this.id = id;
        this.optionText = optionText;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}





