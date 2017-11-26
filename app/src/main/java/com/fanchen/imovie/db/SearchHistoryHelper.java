package com.fanchen.imovie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fanchen.imovie.entity.face.ISearchWord;

import java.util.ArrayList;

/**
 * SearchDialogFragment
 *
 * 搜索历史记录database
 * Created by fanchen on 2017/9/17.
 */
public class SearchHistoryHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "search_history";

    public static final String TABLE_NAME = "tab_search_history";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "history text)";

    public SearchHistoryHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);//创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 查询全部搜索记录
     */
    public ArrayList<ISearchWord> queryAllHistory() {
        ArrayList<String> historys = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            //获取数据库对象
            db = getReadableDatabase();
            //查询表中的数据
            cursor = db.query(TABLE_NAME, null, null, null, null, null, "id desc");
            //获取name列的索引
            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                String history = cursor.getString(1);
                historys.add(history);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }
        ArrayList<ISearchWord> iSearchWords = new ArrayList<>();
        for (final String s : historys) {
            iSearchWords.add(new ISearchWord() {

                @Override
                public int getViewType() {
                    return TYPE_NORMAL;
                }

                @Override
                public int getType() {
                    return TYPE_WORD;
                }

                @Override
                public String getWord() {
                    return s;
                }
            });
        }
        return iSearchWords;
    }

    /**
     * 插入数据到数据库
     */
    public void insertHistory(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("history", keyword);
        //调用insert方法，将数据插入数据库
        db.insert(TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    /**
     * 删除某条数据
     */
    public void deleteHistory(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        db.delete(TABLE_NAME, "history=?", new String[]{keyword});
        //关闭数据库
        db.close();
    }

    /**
     * 删除全部数据
     */
    public void deleteAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        //删除全部数据
        db.execSQL("delete from " + TABLE_NAME);
        //关闭数据库
        db.close();
    }

}
