package com.example.mapplaces.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataBaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private var cal = Calendar.getInstance()

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WeatherDataBase"
        private const val TABLE_PLACES = "PlacesTable"
        private const val TABLE_WEATHER = "WeatherTable"

        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_VALUE = "value"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PLACE_TABLE = ("CREATE TABLE $TABLE_PLACES( $KEY_NAME TEXT PRIMARY KEY," +
                "$KEY_LONGITUDE TEXT, $KEY_LATITUDE TEXT)")
        val CREATE_WEATHER_TABLE = ("CREATE TABLE $TABLE_WEATHER($KEY_ID INTEGER PRIMARY KEY, $KEY_NAME TEXT,"+
                "$KEY_DATE TEXT, $KEY_VALUE TEXT)")
        db?.execSQL(CREATE_PLACE_TABLE)

        val ADD_CURRENT_PLACE = ("INSERT INTO $TABLE_PLACES VALUES ('Текущая локация',0,0)")
        db?.execSQL(ADD_CURRENT_PLACE)
        db?.execSQL(CREATE_WEATHER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WEATHER")
        onCreate(db)
    }
    fun addPlace(name: String, longitude: String, latitude: String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, name)
        contentValues.put(KEY_LONGITUDE, longitude)
        contentValues.put(KEY_LATITUDE, latitude)
        val result = db.insert(TABLE_PLACES, null, contentValues)
        db.close()
        return result
    }
    @SuppressLint("Range")
    fun getPlace(longitude: String, latitude: String): String{
        var result: String  = ""
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLACES WHERE $KEY_LONGITUDE=='$longitude' AND $KEY_LATITUDE=='$latitude'"
        try{
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                result = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            e.printStackTrace()
            db.close()
            return result
        }
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getLongitudeLatitude(place: String): ArrayList<String>{
        var result: ArrayList<String>  = ArrayList<String>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLACES WHERE $KEY_NAME=='$place'"
        try{
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                result.add(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)))
                result.add(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)))
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            e.printStackTrace()
            db.close()
            return result
        }
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getPlacesList(): ArrayList<String>{
        val placeList: ArrayList<String> = ArrayList<String>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLACES"
        try{
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do{
                    val place = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                    placeList.add(place)

                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            e.printStackTrace()
            db.close()
            return ArrayList()
        }
        db.close()
        return placeList
    }

    fun addWeather(place: String, date: String, value: String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, place)
        contentValues.put(KEY_DATE, date)
        contentValues.put(KEY_VALUE, value)
        deleteWeather(place)
        val result = db.insert(TABLE_WEATHER, null, contentValues)
        db.close()
        return result
    }

    private fun deleteWeather(place: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_WEATHER, "$KEY_NAME = '${place}'", null)
    }

    @SuppressLint("Range")
    fun getWeatherToday(place: String): String?{
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        var theWeatherToday: String? = null
        val theDateToday: String = sdf.format(cal.time).toString()

        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_WEATHER WHERE $KEY_NAME=='$place' AND $KEY_DATE=='$theDateToday'"
        try{
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                theWeatherToday = cursor.getString(cursor.getColumnIndex(KEY_VALUE))
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            e.printStackTrace()
            db.close()
            return theWeatherToday
        }
        db.close()
        return theWeatherToday
    }
}