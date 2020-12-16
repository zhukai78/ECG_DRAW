package com.hopetruly.ecg.algorithm;

import android.content.Context;

import com.hopetruly.ecg.ECGApplication;
import com.hopetruly.ecg.util.AlertUtils;
import com.hopetruly.ecg.util.VibratorUtils;

import java.sql.Date;

/* renamed from: com.hopetruly.ecg.algorithm.b */
public class MaybeAlertHelper {

    /* renamed from: c */
    private static int INT_10 = 10;

    /* renamed from: d */
    private static int INT_60 = 60;

    /* renamed from: a */
    ECGApplication mECGApplication;

    /* renamed from: b */
    AlertUtils mAlertUtils;

    /* renamed from: e */
    private int f2736e = INT_60;

    /* renamed from: f */
    private Context mContext;

    /* renamed from: g */
    private int f2738g = 0;

    /* renamed from: h */
    private int[] f2739h = {70, 70, 70, 70, 70, 70, 70, 70, 70, 70};

    /* renamed from: i */
    private long f2740i = 0;

    /* renamed from: j */
    private long f2741j = 0;

    /* renamed from: k */
    private boolean isVibrator;

    /* renamed from: l */
    private boolean f2743l;

    public MaybeAlertHelper(Context context) {
        this.mContext = context;
        this.mECGApplication = (ECGApplication) this.mContext.getApplicationContext();
        this.mAlertUtils = new AlertUtils(this.mContext);
        this.isVibrator = false;
        this.f2743l = false;
    }

    /* renamed from: k */
    private void startVibrator() {
        if (!this.isVibrator) {
            switch (this.mECGApplication.appECGConf.getECG_ALARM_TYPE()) {
                case 0:
                    VibratorUtils.goVibrator(this.mContext, 1, 100);
                    break;
                case 1:
                    break;
                case 2:
                    VibratorUtils.goVibrator(this.mContext, 1, 100);
                    break;
            }
            if (this.mAlertUtils.getmisPlay()) {
                this.mAlertUtils.resumePlay();
            } else {
                this.mAlertUtils.initAlert(-1);
            }
            this.isVibrator = true;
        }
    }

    /* renamed from: a */
    public void pauseVibrator() {
        if (this.isVibrator) {
            switch (this.mECGApplication.appECGConf.getECG_ALARM_TYPE()) {
                case 0:
                    VibratorUtils.canvelVibrator();
                    break;
                case 1:
                    break;
                case 2:
                    VibratorUtils.canvelVibrator();
                    break;
            }
            this.mAlertUtils.pausePlay();
            this.isVibrator = false;
        }
    }

    /* renamed from: a */
    public void mo2450a(int i) {
        if (i == 0) {
            pauseVibrator();
            return;
        }
        this.f2739h[mo2458i()] = i;
        if (mo2457h()) {
            Date date = new Date(System.currentTimeMillis());
            if (this.f2740i == 0) {
                this.f2740i = date.getTime();
            } else {
                this.f2741j = date.getTime();
            }
            if (this.f2740i != 0 && this.f2741j != 0 && this.f2741j - this.f2740i >= ((long) (this.mECGApplication.appECGConf.getECG_ALARM_DELAY() * 1000))) {
                this.f2740i = 0;
                this.f2741j = 0;
                startVibrator();
                return;
            }
            return;
        }
        this.f2740i = 0;
        this.f2741j = 0;
        pauseVibrator();
    }

    /* renamed from: b */
    public boolean getECG_ALARM_ENABLE() {
        return this.mECGApplication.appECGConf.getECG_ALARM_ENABLE();
    }

    /* renamed from: c */
    public void stopAlarm() {
        pauseVibrator();
        this.f2743l = true;
        this.f2740i = 0;
        this.f2741j = 0;
    }

    /* renamed from: d */
    public void mo2453d() {
        this.f2743l = false;
    }

    /* renamed from: e */
    public void mo2454e() {
        if (this.f2743l) {
            this.f2736e--;
            if (this.f2736e == 0) {
                this.f2743l = false;
                this.f2736e = INT_60;
            }
        }
    }

    /* renamed from: f */
    public boolean mo2455f() {
        return this.f2743l;
    }

    /* renamed from: g */
    public boolean getisVibrator() {
        return this.isVibrator;
    }

    /* renamed from: h */
    public boolean mo2457h() {
        if (!this.mECGApplication.appECGConf.getECG_ALARM_ENABLE() || this.f2743l) {
            return false;
        }
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < INT_10; i3++) {
            if (this.f2739h[i3] > this.mECGApplication.appECGConf.getECG_ALARM_RATE_MAX()) {
                i++;
            }
            if (this.f2739h[i3] < this.mECGApplication.appECGConf.getECG_ALARM_RATE_MIN()) {
                i2++;
            }
        }
        return ((double) i) > ((double) INT_10) * 0.7d || ((double) i2) > ((double) INT_10) * 0.7d;
    }

    /* renamed from: i */
    public int mo2458i() {
        if (this.f2738g >= INT_10) {
            this.f2738g = 0;
            return 0;
        }
        int i = this.f2738g;
        this.f2738g = i + 1;
        return i;
    }

    /* renamed from: j */
    public void stopeVibrator() {
        pauseVibrator();
        this.mAlertUtils.clearAndRelease();
    }
}
