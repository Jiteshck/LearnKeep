package com.example.learnkeep;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReminderWorker extends Worker {

    public ReminderWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        String title = getInputData().getString("title");

        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "study_channel")
                        .setSmallIcon(R.drawable.ic_default)
                        .setContentTitle("Study Reminder 📚")
                        .setContentText("Revise: " + title)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
                manager.notify((int) System.currentTimeMillis(), builder.build());
            }
        } else {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            "study_channel",
                            "Study Reminder",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationManager manager =
                    getApplicationContext().getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }
}