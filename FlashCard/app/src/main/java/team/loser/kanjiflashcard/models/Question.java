package team.loser.kanjiflashcard.models;

public class Question {
    private String question, option1, option2, option3, option4;
    private int correctAns;
    private String howToRead, Examples;
    private String definition;

    public Question(String question, String option1, String option2, String option3, String option4, int correctAns, String howToRead, String examples, String definition) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAns = correctAns;
        this.howToRead = howToRead;
        Examples = examples;
        this.definition = definition;
    }

    public Question() {
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getHowToRead() {
        return howToRead;
    }

    public void setHowToRead(String howToRead) {
        this.howToRead = howToRead;
    }

    public String getExamples() {
        return Examples;
    }

    public void setExamples(String examples) {
        Examples = examples;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }
}
