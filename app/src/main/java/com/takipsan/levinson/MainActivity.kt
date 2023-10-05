package com.takipsan.levinson

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.takipsan.levinson.Entities.Retrofit.Request.Login
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.databinding.ActivityMainBinding
import com.takipsan.levinson.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna basarak kapatmasını engelliyor.

        binding.entryBtn.setOnClickListener {
            val loginRequest: Login = Login(
                username = binding.username.text.toString().trim(),
                password = binding.Password.text.toString().trim(),
                secretKey = "takipsanlv"
            )


            viewModel.getLoginInfo(this, loginRequest)
        }

        viewModel.loginInfo.observe(this) {
            if (it.status == Status.NOINTERNET) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Internet")
                    .setMessage("İnternet erişimi yok")
                    .setPositiveButton("Tamam") { dialog, _ ->
                        // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                        dialog.dismiss()
                    }

                    .show()

            } else if (it.status == Status.LOADING) {
                showLoading()

            } else if (it.status == Status.SUCCESS) {
                hideLoading()
                val loginOK: Boolean =
                  it.data!!.data != null && !it.data!!.data.access_token.isNullOrEmpty()
                if (loginOK) {
                    UserData.bearer = "Bearer " + it.data!!.data.access_token
                    UserData.UserID = it.data!!.data.id
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                    this.finish()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Giriş")
                        .setMessage("Hatalı Giriş.")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                            dialog.dismiss()
                        }

                        .show()

                }

            } else {
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