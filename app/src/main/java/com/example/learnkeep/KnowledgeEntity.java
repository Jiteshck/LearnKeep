package com.example.learnkeep;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "knowledge")
public class KnowledgeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String notes;
    public String youtubeLinks;
    public String confidence;
    public String tags;

    // JSON or comma-separated paths
    public String attachmentPaths;

    public long createdAt;
    public int reviewCount;
}
