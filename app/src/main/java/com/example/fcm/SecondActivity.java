package com.example.fcm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		TextView txt = findViewById(R.id.textView);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
            Map<String, String> data = bundle.putSerializable("data");
			for (String key : data.keySet()) {
				Object value = data.get(key);
				txt.append(key + ": " + value + "\n\n");
			}
		}
	}
}