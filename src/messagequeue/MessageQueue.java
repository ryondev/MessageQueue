package messagequeue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {

    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Message messages;
    private boolean quitting;

    public boolean enqueueMessage(Message message) {
        try {
            lock.lockInterruptibly();
            if (quitting) {
                return false;
            }
            insertMessage(message);
            notEmpty.signal();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    private void insertMessage(Message message) {
        Message now = messages;
        if (messages == null || message.when < now.when) {
            message.next = messages;
            messages = message;
            return;
        }
        Message pre = now;
        now = now.next;
        while (now != null && now.when < message.when) {
            pre = now;
            now = now.next;
        }
        message.next = now;
        pre.next = message;
    }

    public Message next() {
        long waitTimeMills = 0;
        while (true) {
            try {
                lock.lockInterruptibly();
                waitMessage(waitTimeMills);
                if (quitting) {
                    return null;
                }
                long now = System.currentTimeMillis();

                Message msg = messages;
                if (msg != null) {
                    if (now < msg.when) {
                        waitTimeMills = msg.when - now;
                    } else {
                        messages = messages.next;
                        msg.next = null;
                        return msg;
                    }
                } else {
                    waitTimeMills = -1;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } finally {
                lock.unlock();
            }
        }
    }

    public void quit() {
        lock.lock();
        quitting = true;
        notEmpty.signal();
        lock.unlock();
    }

    private void waitMessage(long waitTimeMills) throws InterruptedException {
        if (waitTimeMills < 0) {
            return;
        }
        if (waitTimeMills == 0) {
            while (messages == null) {
                notEmpty.await();
            }
        } else {
            notEmpty.await(waitTimeMills, TimeUnit.MILLISECONDS);
        }
    }
}
