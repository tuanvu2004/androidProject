package com.example.mobileapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp.database.entity.DeletedHistory;

import java.util.List;

@Dao
public interface DeletedHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeletedHistory(DeletedHistory deletedHistory);

    @Query("SELECT resultId FROM deleted_history WHERE userEmail = :userEmail")
    List<Long> getDeletedHistoryIds(String userEmail);
}
