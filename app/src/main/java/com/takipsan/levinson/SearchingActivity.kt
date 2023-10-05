package com.takipsan.levinson

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pda.rfid.EPCModel
import com.pda.rfid.IAsynchronousMessage
import com.takipsan.levinson.Adapters.SayimListesiAdapter
import com.takipsan.levinson.Adapters.UrunBulListesiAdapter
import com.takipsan.levinson.Entities.Retrofit.Request.UrunArama
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.databinding.ActivityCountingListBinding
import com.takipsan.levinson.databinding.ActivitySearchingBinding
import com.takipsan.levinson.viewModel.CountingViewModel
import com.takipsan.levinson.viewModel.UrunAramaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchingBinding
    private val viewModel: UrunAramaViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searching)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna basarak kapatmasını engelliyor.
        binding.urunBulBtnGetir.setOnClickListener {
            if (binding.urunBulTxtKeyword.text.toString().length < 3) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("En az 3 harf girmelisiniz!")
                    .setPositiveButton("Tamam", null)
                    .show()
            } else {
                viewModel.getUrunList(this, UrunArama(binding.urunBulTxtKeyword.text.toString()))
            }
        }

        viewModel.urun.observe(this) {
            if (it.status == Status.LOADING) {
                showLoading();
            } else if (it.status == Status.SUCCESS) {
                hideLoading()
                if (it.data!!.code == "00") {
                    val customListAdapter =
                        UrunBulListesiAdapter(this, ArrayList(it.data!!.data))// Adapteri oluştur
                    binding.urunBulListview.adapter = customListAdapter
                    binding.urunBulListview.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(this,EpcFindActivity::class.java)
                        intent.putExtra("epc",it.data!!.data[position].epc)
                        intent.putExtra("code",it.data!!.data[position].code)
                        intent.putExtra("name",it.data!!.data[position].name)
                        startActivity(intent)
                    }
                } else if (it.data!!.code == "01") {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Uyarı")
                        .setMessage("Aradığınız kriterde ürün bulunamadı!")
                        .setPositiveButton("Tamam", null)
                        .show()
                    binding.urunBulListview.adapter = null
                }
            }
            else if(it.status == Status.NOINTERNET){
                hideLoading()
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("Cihaz bağlı değil!")
                    .setPositiveButton("Tamam", null)
                    .show()
            }else if(it.status == Status.FORBIDDEN){
                hideLoading()
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("Oturum süresi doldu veya bir başkası tarafından oturumunuz açıldı")
                    .setPositiveButton("Tamam") { dialog, which ->
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .show()

            }

            else {
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

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }
}