package com.example.mobileapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp.database.entity.LocalTopic;

import java.util.List;

@Dao
public interface LocalTopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTopics(List<LocalTopic> topics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTopic(LocalTopic topic);

    @Query("SELECT * FROM local_topics WHERE userEmail = :userEmail")
    List<LocalTopic> getTopicsForUser(String userEmail);

    @Query("DELETE FROM local_topics WHERE id = :topicId AND userEmail = :userEmail")
    void deleteTopic(Long topicId, String userEmail);

    @Query("DELETE FROM local_topics WHERE userEmail = :userEmail")
    void deleteTopicsForUser(String userEmail);
}
