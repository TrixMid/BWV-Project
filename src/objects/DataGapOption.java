package objects;

public class DataGapOption {
    private String option_text;
    private boolean is_correct;
    private int option_order;

    public DataGapOption(String option_text, boolean is_correct, int option_order) {
        this.option_text = option_text;
        this.is_correct = is_correct;
        this.option_order = option_order;
    }
}
