import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by medivn on 17-3-8.
 */
public class Test {
    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        for (int retryCount = 1; retryCount < 20; retryCount++) {

            long beBackoffSlotInMillis =  TimeUnit.MILLISECONDS.toMillis(100);
            long sleepTimeInMillis = ((long) (random.nextDouble() *
                    (1L << Math.min(retryCount, 20)))) * beBackoffSlotInMillis;
            System.out.println(sleepTimeInMillis);
        }
    }
}
