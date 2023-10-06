package com.takipsan.levinson.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.takipsan.levinson.Entities.Retrofit.Response.ConsigmentEpc
import com.takipsan.levinson.R

class SevkiyatDetayListesiAdapter(
    private val context: Context,
    private val dataList: ArrayList<ConsigmentEpc>
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
        val view = p1 ?: inflater.inflate(R.layout.list_view_row_sayim_detay_listesi, null)
        val Epc =view.findViewById<TextView>(R.id.sayimDetayListesi_txtEpc) as TextView
        val Status =view.findViewById<TextView>(R.id.sayimDetayListesi_txtEpcDurum) as TextView
        Epc.text = dataList[p0].epc
        Status.text = dataList[p0].found.toString()


        return view
    }

}