package messagequeue;

public class Handler {

    private Looper looper;
    private Callback callback;

    public Handler() {
        this(Looper.myLooper(), null);
    }

    public Handler(Looper looper, Callback callback) {
        this.looper = looper;
        this.callback = callback;
    }

    protected void handleMessage(Message message) {

    }

    void dispatchMessage(Message message) {
        if (message.callback != null) {
            message.callback.run();
            return;
        } else {
            if (callback != null) {
                if (callback.handleMessage(message)) {
                    return;
                }
            }
        }
        handleMessage(message);
    }

    public void sendMessage(Message message) {
        if (looper == null) return;
        MessageQueue queue = looper.getMessageQueue();
        message.target = this;
        queue.enqueueMessage(message);
    }

    public void postDelay(Runnable runnable, long delay) {
        Message message = new Message();
        message.when = System.currentTimeMillis() + delay;
        message.callback = runnable;
        sendMessage(message);
    }

    public void sendMessageDelay(Message message, long delay) {
        message.when = System.currentTimeMillis() + delay;
        sendMessage(message);
    }

    public void post(Runnable runnable) {
        postDelay(runnable, 0);
    }

    public interface Callback {
        boolean handleMessage(Message message);
    }
}
