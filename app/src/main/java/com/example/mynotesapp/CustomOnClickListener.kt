package com.example.mynotesapp

import android.view.View
import android.widget.AdapterView

//Kelas di atas bertugas membuat item seperti CardView bisa diklik di dalam adapter.
class CustomOnClickListener(private val position : Int, private val onItemClickCallback : OnItemClickCallback) : View.OnClickListener  {
    override fun onClick(v: View) {
        onItemClickCallback.onItemClicked(v, position)
    }

    interface OnItemClickCallback{
        //Kelas ini dibuat untuk menghindari nilai final dari posisi yang tentunya sangat tidak direkomendasikan.
        fun onItemClicked(view: View, position: Int)
    }

}