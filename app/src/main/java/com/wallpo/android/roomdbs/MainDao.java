package com.wallpo.android.roomdbs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {

    @Insert(onConflict = REPLACE)
    void insert(Notificationdb notificationdb);

    @Delete
    void delete(Notificationdb notificationdb);

    @Delete
    void reset(List<Notificationdb> notificationdb);

    @Query("DELETE FROM notification_db WHERE uid = :uid AND postid = :postID")
    void deletePosts(int uid, String postID);

    @Query("SELECT * FROM notification_db ORDER BY uid DESC LIMIT :limit, 25")
    List<Notificationdb> getAllNotifications(int limit);
}
