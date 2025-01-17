package com.example.fcm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		TextView txt = findViewById(R.id.textView);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			txt.append("\n\n-=> " + df.format(new Date()) + "\n\n");
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				txt.append(key + ": " + value + "\n\n");
			}
		}
	}
}