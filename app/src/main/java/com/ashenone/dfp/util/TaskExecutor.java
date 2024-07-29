package com.ashenone.dfp.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

public class TaskExecutor {
    private Map threadIDMap;
    private SparseArray threadIndexMap;
    private static TaskExecutor INSTANCE;
    private Handler mainHandler;
    private HandlerThread requestThread;
    private HandlerThread callbackThread;
    private HandlerThread uploadCheckerThread;
    private HandlerThread sensorThread;
    private Handler requestHandler;
    private Handler callbackHandler;
    private Handler uploadCheckerHandler;
    private Handler sensorHandler;

    static {
        TaskExecutor.INSTANCE = null;
    }

    private TaskExecutor() {
        this.threadIDMap = new HashMap();
        this.threadIndexMap = new SparseArray();
        this.mainHandler = null;
        this.requestThread = null;
        this.callbackThread = null;
        this.uploadCheckerThread = null;
        this.sensorThread = null;
        this.requestHandler = null;
        this.callbackHandler = null;
        this.uploadCheckerHandler = null;
        this.sensorHandler = null;
    }

    public int getCurrentThreadIndex() {
        return (int)(((Integer)this.threadIDMap.get(Long.valueOf(Thread.currentThread().getId()))));
    }

    public Handler getHandler(int arg2) {
        return (Handler)this.threadIndexMap.get(arg2);
    }

    public void enqueue(Runnable arg8, int arg9) {
        this.enqueue(arg8, arg9, false, 0L, false);
    }

    public void enqueue(Runnable arg8, int arg9, long arg10, boolean arg12) {
        this.enqueue(arg8, arg9, false, arg10, arg12);
    }

    public void enqueue(Runnable runnable, int handlerIndex, boolean postFront, long arg8, boolean removeCallback) {
        Handler handler = this.getHandler(handlerIndex);
        if(handler == null) {
            Log.d("TaskExecutor", "execute failed: known thread flag.");
            return;
        }

        if(removeCallback) {
            handler.removeCallbacks(runnable);
        }

        if(postFront) {
            handler.postAtFrontOfQueue(runnable);  // 插队
            return;
        }

        handler.postDelayed(runnable, arg8);
    }

    public static TaskExecutor getInstance() {
        if(TaskExecutor.INSTANCE == null) {
            Class v1 = TaskExecutor.class;
            synchronized(v1) {
                if(TaskExecutor.INSTANCE == null) {
                    TaskExecutor.INSTANCE = new TaskExecutor();
                }

                return TaskExecutor.INSTANCE;
            }
        }

        return TaskExecutor.INSTANCE;
    }

    public void init() {
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.requestThread = new HandlerThread("request thread");
        this.callbackThread = new HandlerThread("callback thread");
        this.uploadCheckerThread = new HandlerThread("uploadChecker thread");
        this.sensorThread = new HandlerThread("sensor thread");
        this.requestThread.start();
        this.callbackThread.start();
        this.uploadCheckerThread.start();
        this.sensorThread.start();
        this.requestHandler = new Handler(this.requestThread.getLooper());
        this.callbackHandler = new Handler(this.callbackThread.getLooper());
        this.uploadCheckerHandler = new Handler(this.uploadCheckerThread.getLooper());
        this.sensorHandler = new Handler(this.sensorThread.getLooper());
        this.threadIDMap.put(Long.valueOf(this.mainHandler.getLooper().getThread().getId()), Integer.valueOf(3));
        this.threadIDMap.put(Long.valueOf(this.requestHandler.getLooper().getThread().getId()), Integer.valueOf(1));
        this.threadIDMap.put(Long.valueOf(this.callbackHandler.getLooper().getThread().getId()), Integer.valueOf(2));
        this.threadIDMap.put(Long.valueOf(this.uploadCheckerHandler.getLooper().getThread().getId()), Integer.valueOf(4));
        this.threadIDMap.put(Long.valueOf(this.sensorHandler.getLooper().getThread().getId()), Integer.valueOf(5));
        this.threadIndexMap.put(3, this.mainHandler);
        this.threadIndexMap.put(1, this.requestHandler);
        this.threadIndexMap.put(2, this.callbackHandler);
        this.threadIndexMap.put(4, this.uploadCheckerHandler);
        this.threadIndexMap.put(5, this.sensorHandler);
    }
}

