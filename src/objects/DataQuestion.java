package objects;

import javax.persistence.*;

import java.util.ArrayList;

@Entity
@Table(name="question")
public class DataQuestion {
    ArrayList<DataAnswer> answers;

    String quesiton_type;
    String start_text;
    String image_url;
    String end_text;
    boolean allows_multiple;
}
