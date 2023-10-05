package com.takipsan.levinson

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.takipsan.levinson.Adapters.SayimListesiAdapter
import com.takipsan.levinson.Adapters.SevkiyatListesiAdapter
import com.takipsan.levinson.Adapters.UrunBulListesiAdapter
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.databinding.ActivityCountingListBinding
import com.takipsan.levinson.databinding.ActivitySevkiyatBinding
import com.takipsan.levinson.viewModel.ConsigmentViewModel
import com.takipsan.levinson.viewModel.CountingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SevkiyatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySevkiyatBinding
    private val viewModel: ConsigmentViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sevkiyat)
        binding.sayimBtnListeYenile.setOnClickListener {
            viewModel.getSevkiyatList(this)
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna basarak kapatmasını engelliyor.
        viewModel.consigmentList.observe(this) {
            if (it.status == Status.LOADING) {
                showLoading();
            } else if (it.status == Status.SUCCESS) {
                hideLoading()
                if (it.data!!.code == "00") {
                    val customListAdapter =
                        SevkiyatListesiAdapter(this, ArrayList(it.data!!.data))// Adapteri oluştur
                    binding.sevkiyatLvSayimListesi.adapter = customListAdapter
                    binding.sevkiyatLvSayimListesi.setOnItemClickListener { parent, view, position, id ->
                        val selectedItem = it.data!!.data[position].id
                        val intent = Intent(this, SevkiyatDetay::class.java)
                        intent.putExtra("SELECTED_ID", it.data!!.data[position].id)
                        intent.putExtra("SELECTED_NAME",it.data!!.data[position].name)
                        intent.putExtra("SELECTED_MODE",it.data!!.data[position].mode)
                        intent.putExtra("SELECTED_TYPE",it.data!!.data[position].type)
                        startActivity(intent)
                    }
                } else if (it.data!!.code == "01") {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Uyarı")
                        .setMessage("Aradığınız kriterde ürün bulunamadı!")
                        .setPositiveButton("Tamam", null)
                        .show()
                    binding.sevkiyatLvSayimListesi.adapter = null
                }
            } else if (it.status == Status.NOINTERNET) {
                hideLoading()
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("Cihaz bağlı değil!")
                    .setPositiveButton("Tamam", null)
                    .show()
            } else if (it.status == Status.FORBIDDEN) {
                hideLoading()
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("Oturum süresi doldu veya bir başkası tarafından oturumunuz açıldı")
                    .setPositiveButton("Tamam") { dialog, which ->
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .show()

            } else {
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
        viewModel.getSevkiyatList(this)

    }

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }
}