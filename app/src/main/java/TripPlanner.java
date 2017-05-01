import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by albargi on 5/1/2017.
 */

public class TripPlanner extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }
}
