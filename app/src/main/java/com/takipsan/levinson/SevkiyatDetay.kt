package com.takipsan.levinson

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pda.rfid.EPCModel
import com.pda.rfid.IAsynchronousMessage
import com.pda.rfid.uhf.UHFReader
import com.port.Adapt
import com.takipsan.levinson.Adapters.SayimDetayListesiAdapter
import com.takipsan.levinson.Adapters.SevkiyatDetayListesiAdapter
import com.takipsan.levinson.Adapters.SevkiyatListesiAdapter
import com.takipsan.levinson.Entities.Retrofit.Request.ConsignmentEpc
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.Entities.Room.Consignment
import com.takipsan.levinson.databinding.ActivitySevkiyatBinding
import com.takipsan.levinson.databinding.ActivitySevkiyatDetayBinding
import com.takipsan.levinson.viewModel.ConsigmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SevkiyatDetay : AppCompatActivity() ,IAsynchronousMessage{
    private lateinit var binding: ActivitySevkiyatDetayBinding
    private val viewModel: ConsigmentViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    private val TAG = "EPCFIND"
    private var isOpened: Boolean = false;
    private var isReading: Boolean = false
    var toneGenerator: ToneGenerator? = null

    private var Sid = 0
    private var Sname = ""
    private var Smode = 0
    private var type = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sevkiyat_detay)
        Sid = intent.getIntExtra("SELECTED_ID", 0)
        Smode = intent.getIntExtra("SELECTED_ID", 0)
        type = intent.getIntExtra("SELECTED_TYPE", 0)
        Sname = intent.getStringExtra("SELECTED_NAME") ?: ""
        checkPermission()
        binding.pageLabel.text = Sname
        binding.sayimDetayTxtSayimAd.text = if (type == 0) "Kör Sayım" else "Normal Sayım"
        binding.sayimDetayTxtSayimTur.text =
            if (Smode == 0) "Sevkiyat" else "Mal Kabul"

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna basarak kapatmasını engelliyor.

        viewModel.epcList.observe(this) {
            if (it.status == Status.LOADING) {
                showLoading();
            } else if (it.status == Status.SUCCESS) {
                hideLoading()

                viewModel.setFirstCommon(Sid.toLong())
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

        viewModel.commonSayımListesi.observe(this) {
            val adapter = SevkiyatDetayListesiAdapter(this, it)
            binding.sayimDetayLvSayimEpcListesi.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSevkiyatEpcList(this, ConsignmentEpc(Sid), Sid.toLong())
    }

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                CountingDetailActivity.REQUEST_READ_PHONE_STATE
            )
        } else {
            initView()
        }
    }

    private fun initView() {
        Adapt.init(this)
        isOpened = UHFReader.getUHFInstance().OpenConnect(this)
        if (!isOpened) {
            Log.d(TAG, "open UHF failed!")
            // TODO failed opened UHF
        }
        // Set base band auto mode, q=1, session=1, flag = 0 flagA
        UHFReader._Config.SetEPCBaseBandParam(255, 0, 0, 0)
        // set ant 1 power to 20dBm
        UHFReader._Config.SetANTPowerParam(1, 30)
    }



    override fun OutPutEPC(p0: EPCModel?) {

        if (p0 != null) {
            viewModel.setNewItem(p0._EPC,Sid.toLong())
        }
    }


    fun onRead(v: View?) {
        if (!isOpened) {
            return
        }
        if (isReading) {
            return
        }
        // start reading 6C tags using ant 1 in cycle continuous reading mode
        isReading = UHFReader._Tag6C.GetEPC(1,1) == 0
    }

    fun onStop(v: View?) {
        if (!isOpened) {
            return
        }
        if (!isReading) {
            return
        }
        UHFReader.getUHFInstance().Stop()
        isReading = false
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.d(TAG, "onKeyDown keyCode = $keyCode")
        if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() &&
                    (keyCode == KeyEvent.KEYCODE_F9
                            /* RFID Handle button*/ || keyCode == 285 /* Left shortcut*/ || keyCode == 286 /* Right shortcut*/))
            || (Adapt.getSN()
                .startsWith("K3") && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
            || (Adapt.getSN()
                .startsWith("K6") && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
        ) {
            // Press the handle button
            onRead(null)

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        Log.d(TAG, "onKeyUp keyCode = $keyCode")
        if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() &&
                    (keyCode == KeyEvent.KEYCODE_F9
                            /* RFID Handle button*/ || keyCode == 285 /* Left shortcut*/ || keyCode == 286 /* Right shortcut*/))
            || (Adapt.getSN()
                .startsWith("K3") && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
            || (Adapt.getSN()
                .startsWith("K6") && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
        ) {
            // Release the handle button
            onStop(null)

        }
        return super.onKeyUp(keyCode, event)
    }
}