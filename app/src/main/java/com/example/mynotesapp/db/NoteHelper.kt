package com.example.mynotesapp.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion._ID
import java.sql.SQLException

class NoteHelper(context : Context) {
    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE : NoteHelper? = null
        //metode yang nantinya akan digunakan untuk menginisiasi database.
        fun getInstance(context: Context) : NoteHelper =
            INSTANCE ?: synchronized(this) { //Synchronized di sini dipakai untuk menghindari duplikasi instance di semua Thread, karena bisa saja kita membuat instance di Thread yang berbeda.
                INSTANCE ?: NoteHelper(context)
            }

        private lateinit var database: SQLiteDatabase
    }

    init{
        databaseHelper = DatabaseHelper(context)
    }

    //metode untuk membuka dan menutup koneksi ke database-nya.
    @Throws(SQLException::class)
    fun open(){
        database = databaseHelper.writableDatabase
    }

    fun close(){
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }
    //----------------------------------------------------------

    // buat metode untuk melakukan proses CRUD-nya, metode pertama adalah untuk mengambil data.
    fun queryAll() : Cursor{
        return database.query(
            DATABASE_TABLE, null, null, null,null,null, "$_ID ASC")
    }

    //metode untuk mengambil data dengan id tertentu.
    fun queryById(id : String) : Cursor{
        return database.query(DATABASE_TABLE, null, "$_ID = ?", arrayOf(id), null, null, null, null)
    }

    //metode untuk menyimpan data.
    fun insert(values : ContentValues?) :Long{
        return database.insert(DATABASE_TABLE, null, values)
    }

    //memperbarui data
    fun update(id : String, values : ContentValues?) : Int{
        return database.update(DATABASE_TABLE, values, "$_ID =?", arrayOf(id))
    }

    // menghapus data.
    fun deleteById(id: String) :Int{
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
}