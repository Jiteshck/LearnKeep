package com.example.learnkeep;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private final List<KnowledgeEntity> list;
    public TopicAdapter(List<KnowledgeEntity> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        KnowledgeEntity item = list.get(position);

        holder.txtTitle.setText(item.title);
        int iconRes = TopicIconHelper.getIconFromTags(item.tags);
        holder.imgTopic.setImageResource(iconRes);

        String date = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(new Date(item.createdAt));

        holder.txtDate.setText("Added " + date);
        if (item.youtubeLinks != null && !item.youtubeLinks.isEmpty()) {
            holder.txtYoutubePreview.setVisibility(View.VISIBLE);
            holder.txtYoutubePreview.setOnClickListener(v -> {
                String url = item.youtubeLinks;

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            });
        } else {
            holder.txtYoutubePreview.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TopicDetailsActivity.class);
            intent.putExtra("topic_id", item.id);
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtYoutubePreview;
        ImageView imgTopic;
        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgTopic = itemView.findViewById(R.id.imgTopic);
            txtYoutubePreview = itemView.findViewById(R.id.txtYoutubePreview);
        }
    }
}
