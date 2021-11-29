package team.loser.kanjiflashcard.models;

public class Card {
    String id, term, definition, howtoread, examples;
    public  Card(){}
    public Card(String id, String term, String definition, String howtoread, String examples) {
        this.id =id;
        this.term = term;
        this.definition = definition;
        this.howtoread = howtoread;
        this.examples = examples;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getHowtoread() {
        return howtoread;
    }

    public void setHowtoread(String howtoread) {
        this.howtoread = howtoread;
    }

    public String getExamples() {
        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", term='" + term + '\'' +
                ", definition='" + definition + '\'' +
                ", howtoread='" + howtoread + '\'' +
                ", examples='" + examples + '\'' +
                '}';
    }
}
