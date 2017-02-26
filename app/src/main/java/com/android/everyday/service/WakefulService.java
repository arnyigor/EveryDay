package com.android.everyday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class WakefulService extends IntentService {
    public static final String LOCK_NAME_STATIC = "com.android.everyday.service.Static";
    public static final String LOCK_NAME_LOCAL = "com.android.everyday.service.Local";
    private static PowerManager.WakeLock lockStatic = null;
    private PowerManager.WakeLock lockLocal = null;
    public WakefulService(String name) {
        super(name);
    }
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lockLocal = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startId) {
        lockLocal.acquire();
        super.onStart(intent, startId);
        getLock(this).release();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        lockLocal.release();
    }
}