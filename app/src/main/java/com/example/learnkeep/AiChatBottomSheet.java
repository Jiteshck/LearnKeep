package com.example.learnkeep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;


public class AiChatBottomSheet extends BottomSheetDialogFragment {
    private LinearLayout layoutMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ScrollView scrollView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_ai_chat, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutMessages = view.findViewById(R.id.layoutMessages);
        etMessage      = view.findViewById(R.id.etMessage);
        btnSend        = view.findViewById(R.id.btnSend);
        scrollView     = view.findViewById(R.id.scrollView);
        progressBar    = view.findViewById(R.id.progressBar);

        // Welcome message
        addBubble("👋 Hi! I'm your LearnKeep AI. I can see all your topics and help you study smarter. Ask me anything!", false);

        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        etMessage.setText("");
        addBubble(msg, true);
        setLoading(true);

        // Load all topics for context
        new Thread(() -> {
            List<KnowledgeEntity> topics = AppDatabase
                    .getInstance(requireContext())
                    .knowledgeDao()
                    .getAll();

            GeminiService.chat(requireContext(), msg, topics, new GeminiService.ChatCallback() {
                @Override public void onSuccess(String reply) {
                    setLoading(false);
                    addBubble(reply, false);
                }
                @Override public void onError(String error) {
                    setLoading(false);
                    addBubble("⚠️ Sorry, I couldn't connect to AI right now. Please check your API key and internet connection.", false);
                }
            });
        }).start();
    }
    private void addBubble(String text, boolean isUser) {
        requireActivity().runOnUiThread(() -> {
            TextView tv = new TextView(requireContext());
            tv.setText(text);
            tv.setTextSize(15f);
            tv.setPadding(dp(14), dp(10), dp(14), dp(10));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, dp(8));

            if (isUser) {
                tv.setBackgroundResource(R.drawable.bg_chat_user);
                tv.setTextColor(0xFFFFFFFF);
                lp.gravity = android.view.Gravity.END;
                lp.setMarginStart(dp(60));
            } else {
                tv.setBackgroundResource(R.drawable.bg_chat_ai);
                tv.setTextColor(0xFF1E1924);
                lp.gravity = android.view.Gravity.START;
                lp.setMarginEnd(dp(60));
            }
            tv.setLayoutParams(lp);
            layoutMessages.addView(tv);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }
    private void setLoading(boolean loading) {
        requireActivity().runOnUiThread(() ->
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));
    }
    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
