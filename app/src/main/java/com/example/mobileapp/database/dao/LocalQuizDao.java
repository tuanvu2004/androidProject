package com.example.mobileapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp.database.entity.LocalQuizQuestion;

import java.util.List;

@Dao
public interface LocalQuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<LocalQuizQuestion> questions);

    @Query("SELECT * FROM local_quiz_questions WHERE topicId = :topicId")
    List<LocalQuizQuestion> getQuestionsForTopic(Long topicId);

    @Query("DELETE FROM local_quiz_questions WHERE topicId = :topicId")
    void deleteQuestionsForTopic(Long topicId);
}
