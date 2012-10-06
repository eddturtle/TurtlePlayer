package turtle.player.util.dev;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PerformanceMeasure
{
    private static final Map<String, Long> startMillis = new HashMap<String, Long>();

    private static final String TAG = "TurtlePlayer.PerformanceLog";

    public static void start(String id){

        Long dublicated = startMillis.put(id, System.currentTimeMillis());
        if(dublicated != null){
            throw new RuntimeException("chek your calls to PerformanceMeasure with id: " + id);
        }

    }

    public static void stop(String id){
        Long startMillisOfId = startMillis.get(id);

        if(startMillisOfId == null){
            throw new RuntimeException("chek your calls to PerformanceMeasure with id: " + id);
        }

        Long millis = System.currentTimeMillis() - startMillisOfId;

        String formattedTime = String.format("%d min, %d sec, %d millis",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                millis -
                        TimeUnit.SECONDS.toSeconds(TimeUnit.MILLISECONDS.toSeconds(millis))
        );

        Log.v(TAG, id + ": " + formattedTime);

        startMillis.remove(id);
    }

}
