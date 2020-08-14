package com.example.mynotesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotesapp.adapter.NoteAdapter
import com.example.mynotesapp.db.NoteHelper
import com.example.mynotesapp.entity.Note
import com.example.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

//Tugas utama MainActivity ada dua. Pertama, menampilkan data dari
// database pada tabel Note secara ascending. Kedua, menerima nilai balik
// dari setiap aksi dan proses yang dilakukan di NoteAddUpdateActivity.
class MainActivity : AppCompatActivity() {

    companion object{
        private const val EXTRA_STATE= "EXTRA_STATE"// untuk menjaga data saat rotasi dll
    }
    private lateinit var adapter: NoteAdapter
    private lateinit var noteHelper: NoteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Notes"

        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fab_add.setOnClickListener{
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        if(savedInstanceState == null){
            //proses ambil data
            loadNotesAsync()
        }else{
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if(list !=null){
                adapter.listNotes = list
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// pembaharuan atau penghapusan. Metode onActivityResult() akan melakukan penerimaan data dari intent
// yang dikirimkan dan diseleksi berdasarkan jenis requestCode dan resultCode-nya.
        if (data != null){
            when(requestCode){
                // Akan dipanggil jika request codenya ADD
                NoteAddUpdateActivity.REQUEST_ADD -> if(resultCode == NoteAddUpdateActivity.RESULT_ADD){
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)

                    adapter.addItem(note)
                    rv_notes.smoothScrollToPosition(adapter.itemCount-1)
                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }

                //// Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
                NoteAddUpdateActivity.REQUEST_UPDATE->
                    when(resultCode){
                        //Akan dipanggil jika result codenya  UPDATE, Semua data di load kembali dari awal
                    NoteAddUpdateActivity.REQUEST_UPDATE->{
                        val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                        val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0)
                        adapter.updateItem(position, note)
                        rv_notes.smoothScrollToPosition(position)
                        showSnackbarMessage("Satu item berhasil diubah")
                    }

                        //Akan dipanggil jika result codenya DELETE. Delete akan menghapus data dari list berdasarkan dari position
                    NoteAddUpdateActivity.RESULT_DELETE ->{
                        val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                        adapter.removeItem(position)
                        adapter.removeItem(position)
                        showSnackbarMessage("Satu item berhasil dihapus")
                    }
                }
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close() //opennya di onCreate
    }

    //mengambil data dari database dengan menggunakan background thread ||
    //digunakan untuk load data dari tabel dan dan kemudian menampilkannya
    // ke dalam list secara asynchronous dengan menggunakan Background process
    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main){
            progressbar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO){
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await() // menggunakan fungsi async karena kita
            // menginginkan nilai kembalian dari fungsi yang kita panggil.
            // Untuk mendapatkan nilai kembaliannya, kita menggunakan fungsi await().
            if(notes.size>0){
                adapter.listNotes = notes
            }else{
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}