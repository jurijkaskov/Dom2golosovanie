package jurijkaskov.com.dom2golosovanie;

import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by raccoon on 06.04.2015.
 */
public class ThreadControl {
    private final Lock lock = new ReentrantLock();

    private Condition pauseCondition = lock.newCondition();
    private boolean paused = false;
    private boolean cancelled = false;

    public void pause() {
        lock.lock();
        paused = true;
        lock.unlock();
    }

    public void resume() {
        lock.lock();
        try {
            if (!paused) {
                return;
            }
            paused = false;
            pauseCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void cancel() {
        lock.lock();
        try {
            if (cancelled) {
                return;
            }
            cancelled = true;
            pauseCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitIfPaused() throws InterruptedException {
        lock.lock();
        try {
            while (paused && !cancelled) {
                pauseCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
