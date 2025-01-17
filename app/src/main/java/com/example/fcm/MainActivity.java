package com.example.fcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.fcm.R.id.txt;

public class MainActivity extends AppCompatActivity {
	private static final String AUTH_KEY = "key=AAAAQV1Oa0s:APA91bE-9LxKlHNOHjoMp5Mv5JFXGCoxL0he9duCCJIsL3Bo_AIqQL6Ra4Lbgdczv2O7BEtOx-0ZzfXx0B6U8x0Trs_5npih9TD6g2zzZwqxe0geOAA5KyTHwWUK36uQVQAOB4lDUCzR";
	private TextView mTextView;
	private String apkName;
	private String verName;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = findViewById(txt);
		
		try {
			apkName = this.getPackageName();
			verName = this.getPackageManager().getPackageInfo(apkName, 0).versionName;
		}
		catch (Exception e) {}
		mTextView.setText("\n\nBuild by lilac\n\n" + apkName + " : " + verName);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String tmp = "";
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				tmp += key + ": " + value + "\n\n";
			}
			mTextView.setText(tmp);
		}

		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
			@Override
			public void onComplete(@NonNull Task<InstanceIdResult> task) {
				if (!task.isSuccessful()) {
					token = task.getException().toString();
					Log.w("FCM TOKEN Failed", task.getException());
				} else {
					token = task.getResult().getToken();
					Log.i("FCM TOKEN", token);
				}
 				mTextView.setText(token);
			}
		});
	}

	public void showToken(View view) {
		mTextView.setText(token + "\n\nBuild by lilac\n\n" + apkName + " : " + verName);
	}

	public void subscribe(View view) {
		FirebaseMessaging.getInstance().subscribeToTopic("news");
		mTextView.setText(R.string.subscribed);
	}

	public void unsubscribe(View view) {
		FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
		mTextView.setText(R.string.unsubscribed);
	}

	public void sendToken(View view) {
		sendWithOtherThread("token");
	}

	public void sendTokens(View view) {
		sendWithOtherThread("tokens");
	}

	public void sendTopic(View view) {
		sendWithOtherThread("topic");
	}

	private void sendWithOtherThread(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification(type);
			}
		}).start();
	}

	private void pushNotification(String type) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "Send " + type);
			jNotification.put("body", "Firebase Cloud Messaging (App)");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "NOTIFY");
			jNotification.put("icon", "ic_notification");

			//jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");
			jData.put("picture", "https://img2.baidu.com/it/u=3532878409,135137021&fm=253");

			switch(type) {
				case "tokens":
					JSONArray ja = new JSONArray();
					ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
					ja.put(token);
					jPayload.put("registration_ids", ja);
					break;
				case "token":
					jPayload.put("to", token);
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", "/topics/news");
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			mTextView.setText(e.toString());
		}
	}

	private String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next().replace(",", ",\n") : "";
	}
}