package com.example.mobileapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mobileapp.database.dao.LocalQuizDao;
import com.example.mobileapp.database.dao.LocalTopicDao;
import com.example.mobileapp.database.entity.LocalQuizQuestion;
import com.example.mobileapp.database.entity.LocalTopic;

@Database(entities = {LocalQuizQuestion.class, LocalTopic.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract LocalQuizDao localQuizDao();
    public abstract LocalTopicDao localTopicDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "mobile_app_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
