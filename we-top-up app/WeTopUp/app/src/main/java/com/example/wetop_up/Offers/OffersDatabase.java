package com.example.wetop_up.Offers;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Offers.class}, version = 2, exportSchema = false)
public abstract class OffersDatabase extends RoomDatabase {
    public abstract OfferDAO getOfferDao();
}
