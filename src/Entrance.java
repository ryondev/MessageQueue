import messagequeue.Handler;
import messagequeue.Looper;

public class Entrance {
    public static void main(String[] args) {
        Handler handler = new Handler();

        handler.postDelay(() -> System.out.println("im runned!!!!!!"), 2);
        handler.post(() -> System.out.println("yeah!!!!!!"));

        Looper.loop();

    }
}
