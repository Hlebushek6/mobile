package ru.mirea.andreyashkin.favouritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView textView2 = findViewById(R.id.textView2);
            TextView textView3 = findViewById(R.id.textView3);
            String book_name = extras.getString(MainActivity.BOOK_NAME_KEY);
            String quotes_name = extras.getString(MainActivity.QUOTES_KEY);
            textView2.setText("Любимая книга разработчика: " + book_name);
            textView3.setText("Цитата из книги: " + quotes_name);
        }

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editText);
                EditText editText2 = findViewById(R.id.editText2);
                String book = editText.getText().toString();
                String quote = editText2.getText().toString();
                String text = "Название Вашей любимой книги: " + book + ". Цитата: \"" + quote + "\"";

                Intent data = new Intent();
                data.putExtra(MainActivity.USER_MESSAGE, text);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }
}
