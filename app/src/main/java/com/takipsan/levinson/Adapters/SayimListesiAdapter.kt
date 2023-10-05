package com.takipsan.levinson.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.R

class SayimListesiAdapter(
    private val context: Context,
    private val dataList: ArrayList<SayimListesi>
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
        val view = p1 ?: inflater.inflate(R.layout.list_view_row_sayim_listesi, null)
        val sayimAdTxt = view.findViewById<TextView>(R.id.sayimListesi_sayimAd) as TextView
        val sayimTurTxt = view.findViewById<TextView>(R.id.sayimListesi_sayimTur) as TextView
        val sayimSayiTxt = view.findViewById<TextView>(R.id.sayimListesi_sayimSayisi) as TextView
        val sayimDurumTxt = view.findViewById<TextView>(R.id.sayimListesi_sayimDurum) as TextView
        sayimAdTxt.text = dataList[p0].name
        sayimTurTxt.text = if (dataList[p0].mode == 0) "Kör Sayım" else "Normal Sayım"
        sayimSayiTxt.text = dataList[p0].counted.toString()
        sayimDurumTxt.text = if (dataList[p0].status == 0) "Kayıtlı" else "--"
        return view
    }
}