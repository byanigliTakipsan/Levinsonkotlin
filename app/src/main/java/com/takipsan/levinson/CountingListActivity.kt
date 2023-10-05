package com.takipsan.levinson

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.takipsan.levinson.Adapters.SayimListesiAdapter
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.databinding.ActivityCountingListBinding
import com.takipsan.levinson.viewModel.CountingViewModel
import com.takipsan.levinson.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountingListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCountingListBinding
    private val viewModel: CountingViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_counting_list)
        binding.sayimBtnListeYenile.setOnClickListener {
            viewModel.getCountingList(this)
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna basarak kapatmasını engelliyor.
        viewModel.countingList.observe(this){
            if (it.status == Status.LOADING){
                showLoading();
            }
            else if(it.status == Status.SUCCESS){
                hideLoading()
                val customListAdapter = SayimListesiAdapter(this, ArrayList(it.data!!.data) )// Adapteri oluştur
                binding.sayimLvSayimListesi.adapter = customListAdapter
                binding.sayimLvSayimListesi.setOnItemClickListener { parent, view, position, id ->
                    val selectedItem = it.data!!.data[position].id
                    val intent = Intent(this, CountingDetailActivity::class.java)
                    intent.putExtra("SELECTED_ID", it.data!!.data[position].id)
                    intent.putExtra("SELECTED_NAME",it.data!!.data[position].name)
                    intent.putExtra("SELECTED_TYPE",it.data!!.data[position].mode)
                    startActivity(intent)
                }
            }
            else{
                hideLoading()
                MaterialAlertDialogBuilder(this)
                    .setTitle("API HATASI")
                    .setMessage("Lütfen Takipsan ile iletişime geçiniz")
                    .setPositiveButton("Tamam") { dialog, _ ->
                        // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                        dialog.dismiss()
                    }

                    .show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.getCountingList(this)

    }

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

}