package com.example.mynotesapp.helper

import android.database.Cursor
import com.example.mynotesapp.db.DatabaseContract
import com.example.mynotesapp.entity.Note

object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor?) : ArrayList<Note>{
        val noteList = ArrayList<Note>()

        notesCursor?.apply{ //Fungsi apply digunakan untuk menyederhanakan kode yang berulang.
            while (moveToNext()){
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                noteList.add(Note(id, title, description, date))
            }
        }
        return noteList
    }
    // MoveToFirst di sini digunakan untuk memindah cursor ke baris pertama sedangkan MoveToNext
    // digunakan untuk memindahkan cursor ke baris selanjutnya
    // Di sini kita ambil datanya satu per satu dan dimasukkan ke dalam ArrayList.
}