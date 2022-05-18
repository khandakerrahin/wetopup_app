package com.example.wetop_up.Offers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OfferDAO {

    @Insert
    void insert(Offers offers);

    @Query("DELETE FROM Offers")
    void delete();

    @Query("SELECT id,amount,internet,minutes,sms,callRate,validity FROM Offers WHERE operator = :operator AND opType = :opType AND " +
            "(CASE WHEN internet <> '' AND minutes = '' AND sms = '' AND callRate = '' THEN '1' " +
                  "WHEN internet = '' AND minutes = '' AND sms = '' AND callRate <> '' THEN '2' ELSE '3' END) = :query ORDER BY CAST (amount as INTEGER) ASC")
    List<Offers> getOpOffers(String operator, String opType, String query);

    @Query("select * from (select * from Offers where operator = 0 ORDER BY CAST (amount as INTEGER) limit 5) union \n" +
            "select * from (select * from Offers where operator = 1 ORDER BY CAST (amount as INTEGER) limit 5) union \n" +
            "select * from (select * from Offers where operator = 2 ORDER BY CAST (amount as INTEGER) limit 5) union \n" +
            "select * from (select * from Offers where operator = 3 ORDER BY CAST (amount as INTEGER) limit 5) union \n" +
            "select * from (select * from Offers where operator = 4 ORDER BY CAST (amount as INTEGER) limit 5)")
    List<Offers> getAllOffers();

    @Query("SELECT * FROM Offers WHERE id = :id")
    Offers getOfferById(String id);
}
