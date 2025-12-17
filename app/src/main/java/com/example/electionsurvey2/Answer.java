package com.example.electionsurvey2;

import java.util.List;

/**
 * Answer Model Class
 * Stores user's answer to a question
 */
public class Answer {
    private int questionId;
    private String answerText;              // For text questions
    private Integer selectedOptionId;        // For single choice (using Integer to allow null)
    private List<Integer> selectedOptionIds; // For multiple choice

    /**
     * Constructor for text answers
     * @param questionId Question ID
     * @param answerText Text answer
     */
    public Answer(int questionId, String answerText) {
        this.questionId = questionId;
        this.answerText = answerText;
        this.selectedOptionId = null;
        this.selectedOptionIds = null;
    }

    /**
     * Constructor for single choice answers
     * @param questionId Question ID
     * @param selectedOptionId Selected option ID
     */
    public Answer(int questionId, int selectedOptionId) {
        this.questionId = questionId;
        this.answerText = null;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionIds = null;
    }

    /**
     * Constructor for multiple choice answers
     * @param questionId Question ID
     * @param selectedOptionIds List of selected option IDs
     */
    public Answer(int questionId, List<Integer> selectedOptionIds) {
        this.questionId = questionId;
        this.answerText = null;
        this.selectedOptionId = null;
        this.selectedOptionIds = selectedOptionIds;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Integer selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public List<Integer> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Integer> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }
}





