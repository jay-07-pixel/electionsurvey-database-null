package com.example.electionsurvey2;

import java.util.List;

/**
 * Question Model Class
 * Represents a survey question with its options
 */
public class Question {
    private int id;
    private String questionText;
    private String type;  // "text", "single", "multiple"
    private List<Option> options;

    /**
     * Constructor
     * @param id Question ID
     * @param questionText Question text
     * @param type Question type (text, single, multiple)
     * @param options List of options (empty for text questions)
     */
    public Question(int id, String questionText, String type, List<Option> options) {
        this.id = id;
        this.questionText = questionText;
        this.type = type;
        this.options = options;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}





