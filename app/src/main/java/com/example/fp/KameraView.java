package com.example.fp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class KameraView extends AppCompatActivity {
    ImageButton capture, toggleFlash, flipCamera;
    private PreviewView previewView;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;



    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startCamera(cameraFacing);
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kamera_view);

        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        if (ContextCompat.checkSelfPermission(KameraView.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(android.Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        flipCamera.setOnClickListener(view -> {
            if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                cameraFacing = CameraSelector.LENS_FACING_FRONT;
            } else {
                cameraFacing = CameraSelector.LENS_FACING_BACK;
            }
            startCamera(cameraFacing);
        });

//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                bindPreview(cameraProvider);
//            } catch (ExecutionException | InterruptedException e) {
//                // No errors need to be handled for this Future.
//                // This should never be reached.
//            }
//        }, ContextCompat.getMainExecutor(this));

    }

//    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder()
//                .build();
//
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
//    }


    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();
//                PreviewConfig previewConfig = new PreviewConfig().Builder()
//                        .setTargetAspectRatio(AspectRatio.RATIO_4_3) // or AspectRatio.RATIO_16_9
//                        .build();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                capture.setOnClickListener(view -> {
                    if (ContextCompat.checkSelfPermission(KameraView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    takePicture(imageCapture);
                });

                toggleFlash.setOnClickListener(view -> setFlashIcon(camera));

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture) {
//        long timestamp = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);

//      Menggunakan Environment.getExternalStoragePublicDirectory() untuk mendapatkan direktori DCIM pada penyimpanan eksternal umum.
        File externalFilesDir = new File(Environment.DIRECTORY_PICTURES);

        // Buat direktori jika belum ada
        if (externalFilesDir != null && !externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }

        // Buat objek File untuk menyimpan gambar di direktori eksternal aplikasi
        final File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
// Sisanya dari kode tetap sama...
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = getContentResolver().insert(contentUri, values);

//        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        .build();



        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Update the MediaStore with the new image file
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                assert imageUri != null;
                getContentResolver().update(imageUri, values, null, null);

                runOnUiThread(() -> Toast.makeText(KameraView.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show());
                startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> Toast.makeText(KameraView.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                startCamera(cameraFacing);
            }
        });
    }


//    public void takePicture(ImageCapture imageCapture) {
//        final File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
//        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(KameraView.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                startCamera(cameraFacing);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(KameraView.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                startCamera(cameraFacing);
//            }
//        });
//    }

    private void setFlashIcon(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 1) {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            } else {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            }
        } else {
            runOnUiThread(() -> Toast.makeText(KameraView.this, "Flash is not available currently", Toast.LENGTH_SHORT).show());
        }
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}
