package com.fanchen.imovie.db;

import android.content.Context;

import com.litesuits.orm.LiteOrm;

import java.util.HashMap;
import java.util.Map;

/**
 * LiteOrmManager
 * Created by fanchen on 2017/7/25.
 */
public class LiteOrmManager {

    private static LiteOrmManager manager;
    private Context context;

    private Map<String, LiteOrm> map = new HashMap<>();

    /**
     *
     * @param context
     */
    private LiteOrmManager(Context context) {
        this.context = context;
    }

    /**
     *
     * @param db
     * @param isSingle
     * @return
     */
    public synchronized LiteOrm getLiteOrm(String db, boolean isSingle){
        String key = db + isSingle;
        LiteOrm liteOrm = map.get(key);
        if(liteOrm == null){
            if (isSingle) {
                liteOrm = LiteOrm.newSingleInstance(context.getApplicationContext(), db);
            } else {
                liteOrm = LiteOrm.newCascadeInstance(context.getApplicationContext(), db);
            }
            map.put(key, liteOrm);
        }
        return liteOrm;
    }

    /**
     *
     * @param db
     * @return
     */
    public synchronized LiteOrm getLiteOrm(String db){
        return getLiteOrm(db,true);
    }

    /**
     *
     * @param context
     * @return
     */
    public static LiteOrmManager getInstance(Context context){
        if(manager == null){
            synchronized (LiteOrmManager.class){
                if(manager == null){
                    manager = new LiteOrmManager(context.getApplicationContext());
                }
            }
        }
        return manager;
    }

}
