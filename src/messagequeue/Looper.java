package messagequeue;

public class Looper {
    private static ThreadLocal<Looper> localLooper = ThreadLocal.withInitial(Looper::new);
    private MessageQueue queue;

    private Looper() {
        queue = new MessageQueue();
    }

    public static Looper myLooper() {
        return localLooper.get();
    }

    public static void loop() {
        Looper me = myLooper();
        while (true) {
            Message msg = me.queue.next();
            if (msg == null)  {
                System.out.println("msg consumed, looper exit");
                return;
            } else {
                System.out.println("msg revealed");
            }
            msg.target.dispatchMessage(msg);
        }
    }

    MessageQueue getMessageQueue() {
        return queue;
    }

    public void quit() {
        queue.quit();
    }
}
