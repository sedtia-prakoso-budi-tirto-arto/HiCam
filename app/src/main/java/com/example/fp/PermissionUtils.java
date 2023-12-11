package com.example.fp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<String> getMissingPermissions(Context context, String[] permissions) {
        List<String> missingPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        return missingPermissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkAndRequestPermissions(
            Context context, String[] permissions, int requestCode) {
        List<String> missingPermissions = getMissingPermissions(context, permissions);

        if (!missingPermissions.isEmpty()) {
            String[] permissionsToRequest = missingPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(
                    (AppCompatActivity) context,
                    permissionsToRequest,
                    requestCode
            );

            // Tampilkan Toast untuk izin yang belum diizinkan
            showToastForPermissions(context, missingPermissions);

            return false; // Permissions are not granted yet
        } else {
            return true; // All permissions are already granted
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static void showToastForPermissions(Context context, List<String> permissions) {
        StringBuilder toastMessage = new StringBuilder("Izin diperlukan:");
        for (String permission : permissions) {
            toastMessage.append("\n- ").append(permission);
        }

        Toast.makeText(context, toastMessage.toString(), Toast.LENGTH_LONG).show();
    }
}
