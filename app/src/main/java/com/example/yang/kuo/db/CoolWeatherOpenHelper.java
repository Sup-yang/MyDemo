package com.example.yang.kuo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yang on 2017/2/27.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {


    /**
     * Province 表建表语句
     */

    public static final String CREAT_PROVINCE = "create table Province(" + "id integer primary key autoincrement," + "province_name text," + "province_code text)";

    /**
     * City表建表语句
     */

    public static final String CREATE_CITY = "create table City(" + "id integer primary key autoincrement," + "city_name text," + "city_code text," + "province_id integer)";


    /**
     * Country表建表语句
     */
    public static final String CREATE_COUNTY = "create table County(" + "id integer primary key autoincrement," +"county_name text,"+"county_code text,"+"city_id integer)";


    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

   /**
    * 执行创建
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREAT_PROVINCE);//创建province 表
        db.execSQL(CREATE_CITY);//创建city表
        db.execSQL(CREATE_COUNTY);//创建county表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
