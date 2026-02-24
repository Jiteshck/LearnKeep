package com.example.learnkeep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TopicDetailsActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 201;
    private KnowledgeEntity entity;
    private TextView txtConfidenceValue;
    // UI
    private TextView txtAttachmentStatus;
    private EditText etTitle, etNotes;
    private ChipGroup chipGroupTags;
    private RecyclerView recyclerAttachments;
    // Confidence (circle selector)
    private LinearLayout confidenceContainer;
    private int selectedConfidence = 5;
    // Attachments
    private final List<String> attachmentPaths = new ArrayList<>();
    private AttachmentAdapter attachmentAdapter;
    // Camera
    private File cameraPhotoFile;
    //logo switch
    private ImageView imgTopicIcon;
    private LinearLayout tagInputLayout;
    private boolean hasTag = false;
    private TextView txtYoutubeLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_details);

        // Toolbar (NO back arrow)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Views
        etTitle = findViewById(R.id.etTitle);
        etNotes = findViewById(R.id.etNotes);
        etNotes.setMovementMethod(new android.text.method.ScrollingMovementMethod());
        txtYoutubeLink = findViewById(R.id.txtYoutubeLink);
        txtAttachmentStatus = findViewById(R.id.txtAttachmentStatus);
        chipGroupTags = findViewById(R.id.chipGroupTags);
        tagInputLayout = findViewById(R.id.tagInputLayout);
        recyclerAttachments = findViewById(R.id.recyclerAttachments);
        confidenceContainer = findViewById(R.id.confidenceContainer);
        txtConfidenceValue = findViewById(R.id.txtConfidenceValue);
        imgTopicIcon = findViewById(R.id.imgTopicIcon);

        etNotes.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // RecyclerView
        recyclerAttachments.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        attachmentAdapter = new AttachmentAdapter(
                this,
                attachmentPaths,
                updated -> {
                    txtAttachmentStatus.setText(updated.size() + " attachment(s)");
                    entity.attachmentPaths = String.join(",", updated);
                }
        );
        recyclerAttachments.setAdapter(attachmentAdapter);

        // Load topic
        int topicId = getIntent().getIntExtra("topic_id", -1);
        if (topicId == -1) {
            finish();
            return;
        }

        entity = AppDatabase.getInstance(this)
                .knowledgeDao()
                .getById(topicId);

        int iconRes = TopicIconHelper.getIconFromTags(entity.tags);
        imgTopicIcon.setImageResource(iconRes);

        // Populate data
        etTitle.setText(entity.title);
        etNotes.setText(entity.notes);
        txtYoutubeLink.setText(entity.youtubeLinks);

        txtYoutubeLink.setOnClickListener(v -> {
            String url = entity.youtubeLinks;

            if (url != null && !url.isEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        txtYoutubeLink.setOnLongClickListener(v -> {
            String url = entity.youtubeLinks;
            if (url != null && !url.isEmpty()) {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager)
                                getSystemService(CLIPBOARD_SERVICE);

                android.content.ClipData clip =
                        android.content.ClipData.newPlainText("YouTube Link", url);

                clipboard.setPrimaryClip(clip);
                android.widget.Toast.makeText(
                        this,
                        "Link copied to clipboard",
                        android.widget.Toast.LENGTH_SHORT
                ).show();
            }
            return true;
        });

        // Confidence
        try {
            selectedConfidence = Integer.parseInt(entity.confidence);
        } catch (Exception ignored) {
            selectedConfidence = 5;
        }
        setupConfidenceSelector(selectedConfidence);

        // Tags
        if (entity.tags != null) {
            for (String tag : entity.tags.split(",")) {
                if (!tag.trim().isEmpty()) {
                    addTagChip(tag.replace("#", ""));
                }
            }
        }
        EditText etTag = findViewById(R.id.etTag);

        findViewById(R.id.btnAddTag).setOnClickListener(v -> {
            String tag = etTag.getText().toString().trim();
            if (!tag.isEmpty()) {
                addTagChip(tag);
                etTag.setText("");
            }
        });

        // Attachments
        if (entity.attachmentPaths != null) {
            for (String path : entity.attachmentPaths.split(",")) {
                File f = new File(path);
                if (f.exists()) attachmentPaths.add(path);
            }
        }
        updateAttachmentUI();

        // Buttons
        findViewById(R.id.btnCamera).setOnClickListener(v -> openCamera());
        findViewById(R.id.btnImage).setOnClickListener(v -> pickImage());
        findViewById(R.id.btnFile).setOnClickListener(v -> pickFile());

        findViewById(R.id.btnUpdate).setOnClickListener(v -> updateTopic());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteTopic());
    }

    /* ---------------- CONFIDENCE ---------------- */

    private void setupConfidenceSelector(int preselected) {
        confidenceContainer.removeAllViews();

        for (int i = 1; i <= 10; i++) {
            TextView tv = new TextView(this);
            tv.setText(String.valueOf(i));
            tv.setTextColor(Color.WHITE);
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextSize(14f);

            int size = (int) (32 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(size, size);
            params.setMargins(6, 0, 6, 0);
            tv.setLayoutParams(params);

            tv.setBackground(createConfidenceDrawable(i, i == preselected));

            int value = i;
            tv.setOnClickListener(v -> {
                selectedConfidence = value;
                setupConfidenceSelector(value);
            });

            confidenceContainer.addView(tv);
        }

        txtConfidenceValue.setText("Selected: " + preselected);
    }

    private GradientDrawable createConfidenceDrawable(int value, boolean selected) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);

        if (value <= 3) d.setColor(Color.parseColor("#EF4444"));
        else if (value <= 6) d.setColor(Color.parseColor("#F59E0B"));
        else if (value <= 8) d.setColor(Color.parseColor("#84CC16"));
        else d.setColor(Color.parseColor("#22C55E"));

        d.setStroke(selected ? 4 : 2,
                selected ? Color.BLACK : Color.parseColor("#E5E7EB"));
        return d;
    }

    /* ---------------- TAGS ---------------- */

    private void addTagChip(String text) {
        if (hasTag) return;

        Chip chip = new Chip(this);
        chip.setText("#" + text);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(v -> {
            chipGroupTags.removeView(chip);
            hasTag = false;
            tagInputLayout.setVisibility(View.VISIBLE);
        });

        chipGroupTags.addView(chip);
        hasTag = true;
        if (tagInputLayout != null)
            tagInputLayout.setVisibility(View.GONE);
    }

    private String collectTags() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            sb.append(((Chip) chipGroupTags.getChildAt(i)).getText()).append(",");
        }
        return sb.toString();
    }

    /* ---------------- IMAGE PICKER ---------------- */

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            saveToInternal(result.getData().getData());
                        }
                    });

    private void pickImage() {
        imagePicker.launch(
                new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        );
    }

    /* ---------------- FILE PICKER ---------------- */

    private final ActivityResultLauncher<Intent> filePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            saveToInternal(result.getData().getData());
                        }
                    });

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePicker.launch(intent);
    }

    /* ---------------- CAMERA ---------------- */

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                cameraPhotoFile != null &&
                                cameraPhotoFile.exists()) {

                            attachmentPaths.add(cameraPhotoFile.getAbsolutePath());
                            updateAttachmentUI();
                        }
                    });

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        try {
            File dir = new File(getFilesDir(), "attachments");
            if (!dir.exists()) dir.mkdirs();

            cameraPhotoFile = new File(dir, System.currentTimeMillis() + ".jpg");

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    cameraPhotoFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- STORAGE ---------------- */

    private void saveToInternal(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "attachments");
            if (!dir.exists()) dir.mkdirs();

            String ext = "dat";
            String type = getContentResolver().getType(uri);
            if ("image/jpeg".equals(type)) ext = "jpg";
            else if ("image/png".equals(type)) ext = "png";
            else if ("application/pdf".equals(type)) ext = "pdf";

            File file = new File(dir, System.currentTimeMillis() + "." + ext);

            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);

            in.close();
            out.close();

            attachmentPaths.add(file.getAbsolutePath());
            updateAttachmentUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- HELPERS ---------------- */

    private void updateAttachmentUI() {
        txtAttachmentStatus.setText(
                attachmentPaths.size() + " attachment(s)"
        );
        attachmentAdapter.notifyDataSetChanged();
    }

    private void updateTopic() {
        entity.title = etTitle.getText().toString().trim();
        entity.notes = etNotes.getText().toString().trim();
        entity.youtubeLinks = txtYoutubeLink.getText().toString().trim();
        entity.confidence = String.valueOf(selectedConfidence);
        entity.tags = collectTags();
        entity.attachmentPaths = String.join(",", attachmentPaths);

        AppDatabase.getInstance(this)
                .knowledgeDao()
                .update(entity);

        ReminderScheduler.scheduleReminder(
                this,
                entity.id,
                entity.title,
                selectedConfidence
        );
        finish();
    }

    private void deleteTopic() {
        for (String path : attachmentPaths) {
            File f = new File(path);
            if (f.exists()) f.delete();
        }
        ReminderScheduler.cancelReminder(this, entity.id);
        AppDatabase.getInstance(this)
                .knowledgeDao()
                .delete(entity);

        finish();
    }
}
