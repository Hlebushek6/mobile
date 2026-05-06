package ru.mirea.andreyashkin.Lesson4;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.andreyashkin.Lesson4.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.albumText.setText("Ничего не воспроизводится");
        binding.songText.setText("Ничего не воспроизводится");
        binding.artistText.setText("Ничего не воспроизводится");
        binding.pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.pauseButton.getText().equals("Resume")) {
                    binding.pauseButton.setText("Pause");
                    return;
                }
                binding.pauseButton.setText("Resume");
            }
        });
    }
}