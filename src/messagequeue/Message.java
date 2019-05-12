package messagequeue;

public class Message {
    public Object obj;
    public int what;
    Handler target;
    Runnable callback;

    long when;
    Message next;
}
