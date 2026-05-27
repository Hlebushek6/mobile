package ru.mirea.andreyashkingi.mireaproject;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends Fragment {
    private final String TAG = "NewsFragmentTags";
    private MaterialSwitch workerSwitch;
    private SharedPreferences sharedPreferences;

    private ImageView profilePicture;
    private Button takePhotoButton;
    private Button chooseFromGalleryButton;
    private Uri imageUri;

    public SettingsFragment() {
        // Required empty public constructor
    }

    // Регистрируем лаунчер для запроса разрешения
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Log.d(TAG, "Разрешение получено пользователем");
            startPeriodicWorker(); // Запускаем воркер сразу после одобрения
        } else {
            Log.d(TAG, "Пользователь отказал в разрешении");
        }
    }
    );

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                if (isGranted);
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        workerSwitch = view.findViewById(R.id.workerSwitch);

        profilePicture = view.findViewById(R.id.profile_picture);
        takePhotoButton = view.findViewById(R.id.take_photo_button);
        chooseFromGalleryButton = view.findViewById(R.id.choose_from_gallery_button);


        sharedPreferences = requireContext().getSharedPreferences("app_settings",
                Context.MODE_PRIVATE);

        boolean isWorkerEnabled = sharedPreferences.getBoolean("worker_enabled",
                false);

        workerSwitch.setChecked(isWorkerEnabled);

        workerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked);
            if (isChecked) {
                checkPermissionsAndStartWorker();
            } else {
                stopPeriodicWorker();
            }
        });

        ActivityResultCallback<ActivityResult> callback = new ActivityResultCallback<
                ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    profilePicture.setImageURI(imageUri);
                }
            }
        };

        ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), callback);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission
                        .CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    try{
                        File photoFile = createImageFile();

                        String authorities = requireContext().getPackageName() + ".fileprovider";

                        imageUri = FileProvider.getUriForFile(requireContext(), authorities,
                                photoFile);

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        cameraActivityResultLauncher.launch(cameraIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(
                new Date());

        String imageFileName = "IMAGE_" + timeStamp + "_";

        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }

    private void checkPermissionsAndStartWorker() {
        createNotificationChannel();

        // Разрешение POST_NOTIFICATIONS появилось только в Android 13 (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission
                    .POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "Разрешения уже были получены ранее");
                startPeriodicWorker();
            } else {
                Log.d(TAG, "Разрешений нет, запрашиваем...");
                // Запускаем системное окно запроса
                requestNotificationPermissionLauncher.launch(POST_NOTIFICATIONS);
            }
        } else {
            // На Android 12 и ниже разрешение на уведомления включено по умолчанию
            startPeriodicWorker();;
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("my_channel",
                    "My notifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = requireContext().getSystemService(NotificationManager
                    .class);

            manager.createNotificationChannel(channel);
        }
    }

    // Вынесли логику запуска в отдельный метод, чтобы не дублировать код
    private void startPeriodicWorker() {
        WorkRequest testWorkRequest = new OneTimeWorkRequest.Builder(NewsWorker.class).build();
        WorkManager.getInstance(requireContext()).enqueue(testWorkRequest);

        PeriodicWorkRequest newsWorkRequest = new PeriodicWorkRequest.Builder(
                NewsWorker.class, 15, TimeUnit.MINUTES).build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("news_worker",
                // KEEP - не перезапускает воркер, если он уже работает
                ExistingPeriodicWorkPolicy.KEEP,
                newsWorkRequest);

        Log.d(TAG, "Воркер успешно поставлен в очередь");
    }

    private void stopPeriodicWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("news_worker");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                requireContext());

        notificationManager.cancel(42);
    }

    private void saveSwitchState(boolean isEnabled) {
        sharedPreferences.edit().putBoolean("worker_enabled", isEnabled).apply();
    }
}