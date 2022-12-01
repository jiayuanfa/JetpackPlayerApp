package com.example.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.libcommon.AppGlobals;

@Database(entities = {Cache.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase database;

    static {
        database = Room.databaseBuilder(
                AppGlobals.getApplication(),
                CacheDatabase.class,
                "player_cache")
                .allowMainThreadQueries()
                .build();
    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return database;
    }
}
