package com.takipsan.levinson.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.takipsan.levinson.Entities.Retrofit.Response.UrunArama
import com.takipsan.levinson.R

class UrunBulListesiAdapter (
    private val context: Context,
    private val dataList: ArrayList<UrunArama>
) : BaseAdapter() {
    override fun getCount(): Int {
        return dataList.count()
    }

    override fun getItem(p0: Int): Any {
        return dataList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = p1 ?: inflater.inflate(R.layout.list_view_row_urunbul, null)
        val sayimAdTxt = view.findViewById<TextView>(R.id.urunBulListesi_code) as TextView
        val sayimTurTxt = view.findViewById<TextView>(R.id.urunBulListesi_name) as TextView

        sayimAdTxt.text = dataList[p0].code
        sayimTurTxt.text = dataList[p0].name

        return view
    }
}