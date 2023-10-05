package com.takipsan.levinson.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import com.takipsan.levinson.R

class SevkiyatListesiAdapter (
    private val context: Context,
    private val dataList: ArrayList<SevkiyatListesi>
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
        val view = p1 ?: inflater.inflate(R.layout.list_view_row_sevkiyat_listesi, null)
        val sevkiyat_sayimAd = view.findViewById<TextView>(R.id.sevkiyatListesi_sayimAd) as TextView
        val sevkiyatTipTxt = view.findViewById<TextView>(R.id.sevkiyatListesi_sayimTip) as TextView
        val sevkiyatMod = view.findViewById<TextView>(R.id.sevkiyatListesi_sayimMod) as TextView
        val sevkiyatSayiTxt = view.findViewById<TextView>(R.id.sevkiyatListesi_sayimSayisi) as TextView
        val sevkiyatDurumTxt = view.findViewById<TextView>(R.id.sevkiyatListesi_sayimDurum) as TextView
        sevkiyat_sayimAd.text = dataList[p0].name
        sevkiyatTipTxt.text = if (dataList[p0].type == 0) "Kör Sayım" else "Normal Sayım"
        sevkiyatMod.text = if (dataList[p0].mode == 1) "Sevkiyat" else "Mal Kabul"
        sevkiyatSayiTxt.text = dataList[p0].counted.toString()
        sevkiyatDurumTxt.text = if (dataList[p0].status == 0) "Kayıtlı" else "--"
        return view
    }
}