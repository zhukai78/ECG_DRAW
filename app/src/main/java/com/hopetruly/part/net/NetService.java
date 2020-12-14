package com.hopetruly.part.net;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hopetruly.ecg.ECGApplication;
import com.hopetruly.ecg.R;
import com.hopetruly.ecg.entity.ECGRecord;
import com.hopetruly.ecg.p022b.C0740b;
import com.hopetruly.ecg.util.C0771g;
import com.hopetruly.ecg.util.C0776l;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import com.hexin.ecg_hexin_bio.baidu.location.LocationClientOption;

public class NetService extends Service {

    /* renamed from: a */
    String f2947a = "NetService";

    /* renamed from: b */
    ECGApplication f2948b;

    /* renamed from: c */
    Thread f2949c;

    /* renamed from: d */
    boolean f2950d = false;

    /* renamed from: e */
    AsyncTask<String, Void, Boolean> f2951e;

    /* renamed from: f */
    Handler f2952f;

    /* renamed from: g */
    C0787d f2953g;

    /* renamed from: h */
    String f2954h = "http://www.bitsun.com/cloud/apps/ecg/ecg_data_realtime_upload.php";

    /* renamed from: i */
    private final IBinder f2955i = new C0786c();

    /* renamed from: j */
    private BroadcastReceiver f2956j = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                NetService.this.f2948b.f2093n = NetService.this.mo2826b();
                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(new Intent("com.holptruly.ecg.services.NetService.NET_CHANGE"));
            }
        }
    };

    /* renamed from: k */
    private C0784a f2957k;

    /* renamed from: com.hopetruly.part.net.NetService$a */
    class C0784a extends AsyncTask<String, Integer, String> {
        C0784a() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String doInBackground(String... strArr) {
            if (isCancelled()) {
                return null;
            }
            return C0791b.m2875a(strArr[0], strArr[1], strArr[2]);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String str) {
            if (!isCancelled() && str != null) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
                intent.setFlags(268435456);
                NetService.this.startActivity(intent);
                super.onPostExecute(str);
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            Toast.makeText(NetService.this.getApplicationContext(), NetService.this.getString(R.string.p_downloading), 0).show();
            super.onPreExecute();
        }
    }

    /* renamed from: com.hopetruly.part.net.NetService$b */
    class C0785b extends AsyncTask<String, Void, String> {

        /* renamed from: a */
        Intent f2964a = null;

        C0785b() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String doInBackground(String... strArr) {
            if (isCancelled()) {
                return null;
            }
            return C0791b.m2874a(strArr[0], strArr[1]);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String str) {
            Toast makeText;
            if (str != null) {
                String str2 = NetService.this.f2947a;
                Log.i(str2, "result>>" + str);
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    int i = jSONArray.getInt(0);
                    if (i == 0) {
                        this.f2964a = new Intent("com.holptruly.ecg.services.NetService.LOGIN_SUCCESSFUL");
                    } else if (i == 1) {
                        this.f2964a = new Intent("com.holptruly.ecg.services.NetService.LOGIN_FAILE");
                        int i2 = jSONArray.getInt(2);
                        if (i2 == 1) {
                            makeText = Toast.makeText(NetService.this.getApplicationContext(), NetService.this.getString(R.string.p_username_err), 0);
                        } else if (i2 != 999) {
                            Context applicationContext = NetService.this.getApplicationContext();
                            makeText = Toast.makeText(applicationContext, NetService.this.getString(R.string.p_err_code) + i2, 0);
                        } else {
                            makeText = Toast.makeText(NetService.this.getApplicationContext(), NetService.this.getString(R.string.p_pwd_err), 0);
                        }
                        makeText.show();
                    }
                    LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(this.f2964a);
                    this.f2964a = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                this.f2964a = new Intent("com.holptruly.ecg.services.NetService.LOGIN_FAILE");
                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(this.f2964a);
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    /* renamed from: com.hopetruly.part.net.NetService$c */
    public class C0786c extends Binder {
        public C0786c() {
        }

        /* renamed from: a */
        public NetService mo2852a() {
            return NetService.this;
        }
    }

    /* renamed from: com.hopetruly.part.net.NetService$d */
    class C0787d extends Thread {

        /* renamed from: a */
        public final int f2967a = 0;

        /* renamed from: b */
        public final int f2968b = 1;

        /* renamed from: c */
        public final int f2969c = 2;

        /* renamed from: d */
        public final int f2970d = 3;

        /* renamed from: e */
        public final int f2971e = 4;

        /* renamed from: f */
        boolean f2972f = true;

        /* renamed from: g */
        int f2973g = 0;

        /* renamed from: h */
        final int f2974h = 2;

        /* renamed from: i */
        final int f2975i = 25;

        /* renamed from: j */
        int f2976j = 0;

        /* renamed from: k */
        StringBuffer f2977k;

        /* renamed from: l */
        String f2978l;

        /* renamed from: m */
        Looper f2979m;

        /* renamed from: n */
        HttpURLConnection f2980n;

        /* renamed from: o */
        public boolean f2981o;

        /* renamed from: p */
        ArrayList<HashMap<String, String>> f2982p = new ArrayList<>();

        public C0787d(String str) {
            this.f2978l = str;
        }

        /* renamed from: a */
        public void mo2853a() {
            if (this.f2979m != null) {
                this.f2981o = false;
                this.f2979m.quitSafely();
                this.f2979m = null;
                Log.d(NetService.this.f2947a, "RealTimeupdataThread>exit...");
            }
        }

        /* renamed from: a */
        public void mo2854a(int i, int i2) {
            if (isAlive() && this.f2979m != null && NetService.this.f2952f != null) {
                if (this.f2977k == null) {
                    this.f2977k = new StringBuffer();
                    this.f2976j = 0;
                }
                StringBuffer stringBuffer = this.f2977k;
                stringBuffer.append(i + ",");
                this.f2976j = this.f2976j + 1;
                if (this.f2976j == 25) {
                    this.f2977k.deleteCharAt(this.f2977k.lastIndexOf(","));
                    Message obtain = Message.obtain(NetService.this.f2952f);
                    obtain.what = 2;
                    obtain.arg1 = i2;
                    obtain.arg2 = this.f2976j;
                    obtain.obj = this.f2977k.toString();
                    NetService.this.f2952f.sendMessage(obtain);
                    this.f2977k = null;
                }
            }
        }

        /* renamed from: b */
        public void mo2855b() {
            int i;
            if (isAlive()) {
                Message obtain = Message.obtain(NetService.this.f2952f);
                obtain.what = 1;
                if (this.f2976j <= 0 || this.f2977k == null) {
                    obtain.obj = "0";
                    obtain.arg1 = 75;
                    i = 0;
                } else {
                    this.f2977k.deleteCharAt(this.f2977k.lastIndexOf(","));
                    obtain.obj = this.f2977k.toString();
                    obtain.arg1 = 75;
                    i = this.f2976j;
                }
                obtain.arg2 = i;
                NetService.this.f2952f.sendMessageDelayed(obtain, 1000);
                this.f2977k = null;
            }
        }

        public void run() {
            Looper.prepare();
            this.f2979m = Looper.myLooper();
            NetService.this.f2952f = new Handler(this.f2979m) {
                /* JADX WARNING: Removed duplicated region for block: B:82:? A[RETURN, SYNTHETIC] */
                public void handleMessage(Message message) {
                    String str;
                    String str2;
                    switch (message.what) {
                        case 2:
                            Log.e(NetService.this.f2947a, "RealTimeupdataThread > addData~~~~");
                            HashMap hashMap = new HashMap();
                            hashMap.put("data", (String) message.obj);
                            hashMap.put("heartrate", message.arg1 + "");
                            hashMap.put("len", message.arg2 + "");
                            C0787d.this.f2982p.add(hashMap);
                            if (C0787d.this.f2972f) {
                                C0787d.this.f2972f = false;
                                break;
                            } else {
                                return;
                            }
                        case 3:
                            Log.e(NetService.this.f2947a, "RealTimeupdataThread > reUpload~~~~");
                            if (C0787d.this.f2973g < 2) {
                                C0787d.this.f2973g++;
                                Message obtain = Message.obtain(NetService.this.f2952f);
                                obtain.copyFrom(message);
                                obtain.what = 0;
                                NetService.this.f2952f.sendMessage(obtain);
                                return;
                            }
                            break;
                        case 4:
                            Log.e(NetService.this.f2947a, "RealTimeupdataThread > getNext~~~~");
                            if (!C0787d.this.f2982p.isEmpty()) {
                                C0787d.this.f2973g = 0;
                                Message obtain2 = Message.obtain(NetService.this.f2952f);
                                HashMap hashMap2 = C0787d.this.f2982p.get(0);
                                obtain2.what = 0;
                                obtain2.arg1 = Integer.valueOf((String) hashMap2.get("heartrate")).intValue();
                                obtain2.arg2 = Integer.valueOf((String) hashMap2.get("len")).intValue();
                                obtain2.obj = hashMap2.get("data");
                                NetService.this.f2952f.sendMessage(obtain2);
                                C0787d.this.f2982p.remove(0);
                                return;
                            }
                            Log.e(NetService.this.f2947a, "MyMsgList.isEmpty()");
                            C0787d.this.f2972f = true;
                            return;
                        default:
                            Log.e(NetService.this.f2947a, "RealTimeupdataThread > default~~~~");
                            Intent intent = new Intent();
                            try {
                                C0787d.this.f2980n = (HttpURLConnection) new URL(C0787d.this.f2978l).openConnection();
                                C0787d.this.f2980n.setReadTimeout(3000);
                                C0787d.this.f2980n.setConnectTimeout(3000);
                                C0787d.this.f2980n.setDoInput(true);
                                C0787d.this.f2980n.setDoOutput(true);
                                C0787d.this.f2980n.setUseCaches(false);
                                C0787d.this.f2980n.setRequestMethod("POST");
                                C0787d.this.f2980n.setRequestProperty("Connection", "Keep-Alive");
                                List<Cookie> cookies = C0790a.m2869a().getCookieStore().getCookies();
                                StringBuffer stringBuffer = new StringBuffer();
                                for (Cookie cookie : cookies) {
                                    stringBuffer.append(cookie.getName());
                                    stringBuffer.append("=");
                                    stringBuffer.append(cookie.getValue());
                                    stringBuffer.append(";");
                                }
                                if (stringBuffer.length() > 0) {
                                    stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(";"));
                                }
                                C0787d.this.f2980n.setRequestProperty("Cookie", stringBuffer.toString());
                                String str3 = (String) message.obj;
                                int i = message.arg1;
                                StringBuffer stringBuffer2 = new StringBuffer();
                                stringBuffer2.append("status");
                                stringBuffer2.append("=");
                                stringBuffer2.append(message.what + "");
                                stringBuffer2.append("&");
                                stringBuffer2.append("userId");
                                stringBuffer2.append("=");
                                stringBuffer2.append(NetService.this.f2948b.f2081b.getId());
                                if (NetService.this.f2948b.f2080a != null) {
                                    stringBuffer2.append("&");
                                    stringBuffer2.append("machineId");
                                    stringBuffer2.append("=");
                                    stringBuffer2.append(NetService.this.f2948b.f2080a.getId());
                                }
                                stringBuffer2.append("&");
                                stringBuffer2.append("heartRate");
                                stringBuffer2.append("=");
                                stringBuffer2.append(i);
                                stringBuffer2.append("&");
                                stringBuffer2.append("realTimeData");
                                stringBuffer2.append("=");
                                stringBuffer2.append(str3);
                                stringBuffer2.append("&");
                                stringBuffer2.append("length");
                                stringBuffer2.append("=");
                                stringBuffer2.append(message.arg2 + "");
                                C0787d.this.f2980n.connect();
                                OutputStream outputStream = C0787d.this.f2980n.getOutputStream();
                                outputStream.write(stringBuffer2.toString().getBytes());
                                outputStream.flush();
                                Log.i(NetService.this.f2947a, "post>" + stringBuffer2.toString());
                                if (C0787d.this.f2980n.getResponseCode() == 200) {
                                    String str4 = new String(C0776l.m2818a(C0787d.this.f2980n.getInputStream()), Charset.forName("utf-8"));
                                    Log.e(NetService.this.f2947a, "RealTimeupdataThread>>" + str4);
                                    JSONArray jSONArray = new JSONArray(str4);
                                    int i2 = jSONArray.getInt(0);
                                    if (i2 == 0) {
                                        if (message.what == 0) {
                                            sendEmptyMessage(4);
                                            intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_SUCCESS");
                                            LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                            Log.i(NetService.this.f2947a, "RealTimeupdataThread>code:" + C0787d.this.f2980n.getResponseCode() + "\ninfo:(getnext) ----- code>" + i2);
                                        } else {
                                            int i3 = message.what;
                                        }
                                        C0787d.this.f2980n.disconnect();
                                        if (message.what != 1) {
                                            return;
                                        }
                                        C0787d.this.mo2853a();
                                        return;
                                    }
                                    Message obtain3 = Message.obtain(NetService.this.f2952f);
                                    obtain3.copyFrom(message);
                                    obtain3.what = 3;
                                    sendMessage(obtain3);
                                    str = NetService.this.f2947a;
                                    str2 = "RealTimeupdataThread>code:" + C0787d.this.f2980n.getResponseCode() + "\ninfo:(reUpload) ----- code>" + jSONArray.getInt(0);
                                } else {
                                    Message obtain4 = Message.obtain(NetService.this.f2952f);
                                    obtain4.copyFrom(message);
                                    obtain4.what = 3;
                                    sendMessage(obtain4);
                                    intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_FAIL");
                                    LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                    str = NetService.this.f2947a;
                                    str2 = "RealTimeupdataThread>code:" + C0787d.this.f2980n.getResponseCode() + "\ninfo:";
                                }
                                Log.w(str, str2);
                                C0787d.this.f2980n.disconnect();
                                if (message.what != 1) {
                                }
                            } catch (MalformedURLException e) {
                                intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_FAIL");
                                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                e.printStackTrace();
                                if (message.what != 1) {
                                    return;
                                }
                            } catch (UnknownHostException e2) {
                                intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_FAIL");
                                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                Message obtain5 = Message.obtain(NetService.this.f2952f);
                                obtain5.copyFrom(message);
                                obtain5.what = 3;
                                sendMessage(obtain5);
                                Log.w(NetService.this.f2947a, "RealTimeupdataThread> UnknownHostException");
                                e2.printStackTrace();
                                if (message.what != 1) {
                                    return;
                                }
                            } catch (SocketTimeoutException e3) {
                                intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_FAIL");
                                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                Message obtain6 = Message.obtain(NetService.this.f2952f);
                                obtain6.copyFrom(message);
                                obtain6.what = 3;
                                sendMessage(obtain6);
                                Log.w(NetService.this.f2947a, "RealTimeupdataThread> SocketTimeoutException");
                                e3.printStackTrace();
                                if (message.what != 1) {
                                    return;
                                }
                            } catch (IOException e4) {
                                intent.setAction("com.holptruly.ecg.services.NetService.CONNECT_REMOTE_HOST_FAIL");
                                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                                e4.printStackTrace();
                                if (message.what != 1) {
                                    return;
                                }
                            } catch (JSONException e5) {
                                e5.printStackTrace();
                                if (message.what != 1) {
                                    return;
                                }
                            } catch (Throwable th) {
                                if (message.what == 1) {
                                    C0787d.this.mo2853a();
                                }
                                throw th;
                            }
                            C0787d.this.mo2853a();
                            return;
                    }
                    NetService.this.f2952f.sendEmptyMessage(4);
                }
            };
            this.f2981o = true;
            Looper.loop();
        }
    }

    /* renamed from: com.hopetruly.part.net.NetService$e */
    class C0789e extends AsyncTask<String, Void, String> {
        C0789e() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String doInBackground(String... strArr) {
            if (isCancelled()) {
                Log.e(NetService.this.f2947a, "isCancelled()");
                return null;
            } else if (strArr[0] == null || strArr[1] == null) {
                return null;
            } else {
                return C0791b.m2883c(strArr[0], strArr[1]);
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String str) {
            if (str != null) {
                String str2 = NetService.this.f2947a;
                Log.i(str2, "result>>" + str);
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    int i = jSONArray.getInt(0);
                    if (i == 0) {
                        Log.e(NetService.this.f2947a, "Device Id upload successed");
                        LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(new Intent("com.holptruly.ecg.services.NetService.UPLOAD_ID_SUCCESS"));
                    } else if (i == 1) {
                        int i2 = jSONArray.getInt(2);
                        if (i2 != 999) {
                            String str3 = NetService.this.f2947a;
                            Log.e(str3, NetService.this.getString(R.string.p_err_code) + i2);
                            return;
                        }
                        Log.e(NetService.this.f2947a, "Device Id upload failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(NetService.this.f2947a, "result is null");
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: d */
    public File m2844d(String str, String str2) {
        try {
            HttpGet httpGet = new HttpGet(str2);
            C0790a a = C0790a.m2869a();
            HttpParams params = a.getParams();
            HttpConnectionParams.setSoTimeout(params, 360000);
            a.setParams(params);
            HttpResponse execute = a.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {
                InputStream content = execute.getEntity().getContent();
                StringBuffer stringBuffer = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
                stringBuffer.append(File.separator);
                stringBuffer.append("hopetruly");
                stringBuffer.append(File.separator);
                stringBuffer.append("ECGdata");
                String stringBuffer2 = stringBuffer.toString();
                if (content != null) {
                    File file = new File(stringBuffer2);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(file, str);
                    if (!file2.exists()) {
                        file2.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = content.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (content != null) {
                        content.close();
                    }
                    return file2;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e2) {
            e2.printStackTrace();
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            return null;
        } catch (Exception e4) {
            e4.printStackTrace();
            return null;
        }
        return null;
    }

    /* renamed from: a */
    public void mo2820a(final ECGRecord eCGRecord) {
        if (this.f2950d) {
            Toast.makeText(getApplicationContext(), getString(R.string.uploading), 0).show();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("com.holptruly.ecg.services.NetService.BEGIN_UPLOAD_ACTION"));
        this.f2949c = new Thread(new Runnable() {
            public void run() {
                LocalBroadcastManager a;
                NetService.this.f2950d = true;
                Intent intent = new Intent();
                if (NetService.this.mo2824a()) {
                    String b = null;
                    try {
                        b = C0791b.m2881b("ecg", eCGRecord.getFilePath(), "001");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (b == null) {
                        intent.setAction("com.holptruly.ecg.services.NetService.END_FAIL_UPLOAD_ACTION");
                        LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                        return;
                    }
                    try {
                        String str = NetService.this.f2947a;
                        Log.i(str, "response>" + b);
                        JSONArray jSONArray = new JSONArray(b);
                        int i = jSONArray.getInt(0);
                        if (i == 0) {
                            intent.setAction("com.holptruly.ecg.services.NetService.END_SUCCESS_UPLOAD_ACTION");
                            a = LocalBroadcastManager.getInstance(NetService.this.getApplicationContext());
                        } else if (i == 1 && jSONArray.getInt(2) == 998) {
                            intent.setAction("com.holptruly.ecg.services.NetService.NEED_LOGIN");
                            a = LocalBroadcastManager.getInstance(NetService.this.getApplicationContext());
                        } else if (i == 1 && jSONArray.getInt(2) == 1) {
                            intent.setAction("com.holptruly.ecg.services.NetService.END_FAIL_UPLOAD_EXIST_ACTION");
                            a = LocalBroadcastManager.getInstance(NetService.this.getApplicationContext());
                        } else {
                            intent.setAction("com.holptruly.ecg.services.NetService.END_FAIL_UPLOAD_ACTION");
                            a = LocalBroadcastManager.getInstance(NetService.this.getApplicationContext());
                        }
                        a.sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        intent.setAction("com.holptruly.ecg.services.NetService.END_FAIL_UPLOAD_ACTION");
                        LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                    }
                    NetService.this.f2950d = false;
                } else {
                    intent.setAction("com.holptruly.ecg.services.NetService.END_FAIL_UPLOAD_ACTION");
                    LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(intent);
                }
                NetService.this.f2950d = false;
            }
        });
        this.f2949c.start();
    }

    /* renamed from: a */
    public void mo2821a(String str) {
        this.f2951e = new AsyncTask<String, Void, Boolean>() {

            /* renamed from: a */
            Intent f2961a = new Intent();

            /* access modifiers changed from: protected */
            /* renamed from: a */
            public Boolean doInBackground(String... strArr) {
                Intent intent = new Intent();
                String str = "";
                String a = C0791b.m2873a(strArr[0]);
                if (a != null) {
                    C0771g.m2784a(NetService.this.f2947a, "json>>" + a);
                    try {
                        JSONArray jSONArray = new JSONArray(a);
                        int i = jSONArray.getInt(0);
                        if (i == 0 && NetService.this.mo2824a()) {
                            JSONArray jSONArray2 = jSONArray.getJSONArray(2);
                            ArrayList arrayList = new ArrayList();
                            for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                                JSONArray jSONArray3 = jSONArray2.getJSONArray(i2);
                                ECGRecord eCGRecord = new ECGRecord();
                                eCGRecord.setNetId(jSONArray3.getString(0));
                                eCGRecord.setFileName(jSONArray3.getString(1));
                                eCGRecord.setNetPath(jSONArray3.getString(2));
                                arrayList.add(eCGRecord);
                            }
                            C0740b bVar = new C0740b(NetService.this.getApplicationContext());
                            ArrayList arrayList2 = new ArrayList();
                            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                                ECGRecord eCGRecord2 = (ECGRecord) arrayList.get(i3);
                                if (!bVar.mo2473b(eCGRecord2.getFileName())) {
                                    File a2 = NetService.this.m2844d(eCGRecord2.getFileName(), eCGRecord2.getNetPath());
                                    if (a2 != null) {
                                        C0771g.m2784a(NetService.this.f2947a, "local file Name >>" + a2.getAbsolutePath());
                                        eCGRecord2.setFilePath(a2.getAbsolutePath());
                                        eCGRecord2.setUser(NetService.this.f2948b.f2081b);
                                        eCGRecord2.setMachine(NetService.this.f2948b.f2080a);
                                        eCGRecord2.setTime("接口返回");
                                        eCGRecord2.setPeriod("接口返回");
                                        arrayList2.add(eCGRecord2);
                                    }
                                } else if (eCGRecord2 != null) {
                                    StringBuffer stringBuffer = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
                                    stringBuffer.append(File.separator);
                                    stringBuffer.append("hopetruly");
                                    stringBuffer.append(File.separator);
                                    stringBuffer.append("ECGdata");
                                    if (!new File(stringBuffer.toString(), eCGRecord2.getFileName()).exists()) {
                                        File unused = NetService.this.m2844d(eCGRecord2.getFileName(), eCGRecord2.getNetPath());
                                    }
                                }
                            }
                            this.f2961a.putExtra("records", arrayList2);
                            this.f2961a.setAction("com.holptruly.ecg.services.NetService.SYNC_DATA_SUCCESS_ACTION");
                        }
                        if (i == 1 && jSONArray.getInt(2) == 998) {
                            intent = this.f2961a;
                            str = "com.holptruly.ecg.services.NetService.NEED_LOGIN";
                        } else if (i == 1) {
                            C0771g.m2785b(NetService.this.f2947a, "error code : " + jSONArray.getInt(2));
                            intent = this.f2961a;
                            str = "com.holptruly.ecg.services.NetService.SYNC_DATA_FAIL_ACTION";
                        }
                        intent.setAction(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.f2961a.setAction("com.holptruly.ecg.services.NetService.SYNC_DATA_FAIL_ACTION");
                    }
                }
                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(this.f2961a);
                return true;
            }

            /* access modifiers changed from: protected */
            /* renamed from: a */
            public void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
            }

            /* access modifiers changed from: protected */
            public void onPreExecute() {
                this.f2961a.setAction("com.holptruly.ecg.services.NetService.SYNC_DATA_BEGIN_ACTION");
                LocalBroadcastManager.getInstance(NetService.this.getApplicationContext()).sendBroadcast(this.f2961a);
                super.onPreExecute();
            }
        }.execute(new String[]{str});
    }

    /* renamed from: a */
    public void mo2822a(String str, String str2, String str3) {
        if (this.f2957k == null || this.f2957k.getStatus() != AsyncTask.Status.RUNNING) {
            this.f2957k = new C0784a();
            this.f2957k.execute(new String[]{str, str2, str3});
        }
    }

    /* renamed from: a */
    public void mo2823a(int[] iArr, int i) {
        if (this.f2953g.f2981o) {
            for (int a : iArr) {
                this.f2953g.mo2854a(a, i);
            }
        }
    }

    /* renamed from: a */
    public boolean mo2824a() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0064  */
    /* renamed from: a */
    public boolean mo2825a(String str, String str2) {
        Boolean bool;
        String str3;
        String str4;
        Boolean.valueOf(false);
        String string = this.f2948b.f2084e.getString("MacAddress", (String) null);
        String string2 = this.f2948b.f2084e.getString("DEVICE_ID", this.f2948b.f2083d.mo2690e());
        if (string2 == null || str == null || string == null) {
            Log.i(this.f2947a, "oldId or newId or mac is null");
            return false;
        }
        String replaceAll = string.replaceAll(":", "");
        if (replaceAll.equals(str) && string2.equalsIgnoreCase(str)) {
            str3 = this.f2947a;
            str4 = "MAC与新旧设备id一致";
        } else if (replaceAll.equals(str)) {
            str3 = this.f2947a;
            str4 = "MAC与新设备id一致";
        } else {
            Log.i(this.f2947a, "不上传设备id");
            bool = false;
            if (bool.booleanValue()) {
                mo2827b(str, str2);
            }
            return true;
        }
        Log.i(str3, str4);
        bool = true;
        if (bool.booleanValue()) {
        }
        return true;
    }

    /* renamed from: b */
    public int mo2826b() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return -1;
        }
        if (activeNetworkInfo.getType() == 1) {
            return 1;
        }
        return activeNetworkInfo.getType() == 0 ? 0 : -1;
    }

    /* renamed from: b */
    public void mo2827b(String str, String str2) {
        new C0789e().execute(new String[]{str, str2});
    }

    /* renamed from: c */
    public void mo2828c() {
        if (!mo2831e()) {
            this.f2953g = new C0787d(this.f2954h);
            this.f2953g.start();
        }
    }

    /* renamed from: c */
    public void mo2829c(String str, String str2) {
        if (str == null || str2 == null) {
//            C0140d.m485a(getApplicationContext()).mo390a(new Intent("com.holptruly.ecg.services.NetService.LOGIN_FAILE"));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("com.holptruly.ecg.services.NetService.LOGIN_FAILE"));
            return;
        }
        new C0785b().execute(new String[]{str, str2});
    }

    /* renamed from: d */
    public void mo2830d() {
        if (mo2831e()) {
            this.f2953g.mo2855b();
        }
    }

    /* renamed from: e */
    public boolean mo2831e() {
        return this.f2953g != null && this.f2953g.f2981o;
    }

    public IBinder onBind(Intent intent) {
        return this.f2955i;
    }

    public void onCreate() {
        super.onCreate();
        this.f2948b = (ECGApplication) getApplication();
        this.f2948b.f2093n = mo2826b();
//        C0140d.m485a(getApplicationContext()).mo389a(this.f2956j, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.f2956j);
        super.onDestroy();
    }
}
