package objects;

public class DataAnswer {
    private String option_text;
    private int points;
    private boolean is_correct;
    private int option_order;

    public DataAnswer(String text, int points, boolean is_correct, int option_order){
        this.option_text = text;
        this.points = points;
        this.is_correct = is_correct;
        this.option_order = option_order;
    }
}
