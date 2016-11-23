package tk.site_guru.kinopoiskkodekolomatskiy;

import java.util.Comparator;

/**
 * Created by Alex on 08.10.2016.
 */
public class RatingCompare implements Comparator<Film> {
    @Override
    public int compare(Film one, Film two) {
        Double tr = one.getRating();
        Double tw = two.getRating();
        return tr.compareTo(tw);
    }



    //@Override
    public int compare(String lhs, String rhs) {
        return 0;
    }
}
