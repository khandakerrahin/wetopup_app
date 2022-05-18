package com.example.wetop_up.Offers;

import android.content.Context;

import androidx.room.Room;

public class Connections {
    private static Connections instance;
    private OffersDatabase database;

    private Connections(Context context){
        database = Room.databaseBuilder(context, OffersDatabase.class, "db_offers")
//                .allowMainThreadQueries()
                .build();
    }

    public static Connections getInstance(Context context){
        synchronized (Connections.class){
            if(instance == null){
                instance = new Connections(context);
            }
            return instance;
        }
    }

    public OffersDatabase getDatabase(){
        return database;
    }
}
