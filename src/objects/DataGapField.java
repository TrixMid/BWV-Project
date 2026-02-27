package objects;

import javax.persistence.*;

@Entity
@Table(name="gap_field")
public class DataGapField {
    private int gap_index;
    private String input_type;
    private String correct_type;
    private boolean case_sensitive;

    DataGapField(int gap_index, String input_type, String correct_type, boolean case_sensitive) {
        this.gap_index = gap_index;
        this.input_type = input_type;
        this.correct_type = correct_type;
        this.case_sensitive = case_sensitive;
    }
}
