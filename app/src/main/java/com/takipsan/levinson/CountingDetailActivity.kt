package com.takipsan.levinson

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pda.rfid.EPCModel
import com.pda.rfid.IAsynchronousMessage
import com.pda.rfid.uhf.UHFReader
import com.port.Adapt
import com.takipsan.levinson.Adapters.SayimDetayListesiAdapter
import com.takipsan.levinson.Adapters.SayimListesiAdapter
import com.takipsan.levinson.Entities.Retrofit.Request.CountingEpc
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.databinding.ActivityCountingDetailBinding
import com.takipsan.levinson.viewModel.CountingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import kotlin.properties.Delegates

@AndroidEntryPoint
class CountingDetailActivity : AppCompatActivity(), IAsynchronousMessage {
    private lateinit var binding: ActivityCountingDetailBinding
    private var countingID by Delegates.notNull<Int>()
    private var mode by Delegates.notNull<Int>()
    private val TAG = "Demo"
    private var isOpened: Boolean = false;
    private var isReading: Boolean = false
    private val viewModel: CountingViewModel by viewModels()
    private lateinit var context: Context
    private lateinit var progressDialog: ProgressDialog
    private var list: ArrayList<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc> =
        ArrayList()

    private var yeniItemlist: ArrayList<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc> =
        ArrayList()
    val mainScope = CoroutineScope(Dispatchers.Main) // UI thread üzerinde çalışacak
    private lateinit var adapter: SayimDetayListesiAdapter

    private fun initView() {
        Adapt.init(this)
        isOpened = UHFReader.getUHFInstance().OpenConnect(this)
        if (!isOpened) {
            Log.d(TAG, "open UHF failed!")
            // TODO failed opened UHF
        }
        // Set base band auto mode, q=1, session=1, flag = 0 flagA
        UHFReader._Config.SetEPCBaseBandParam(255, 0, 1, 0)
        // set ant 1 power to 20dBm
        UHFReader._Config.SetANTPowerParam(1, 30)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_READ_PHONE_STATE
            )
        } else {
            initView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UHFReader.getUHFInstance().CloseConnect();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_counting_detail)
        context = this
        binding.sayimDetayTxtSayimAd.text = intent.getStringExtra("SELECTED_NAME")
        mode = intent.getIntExtra("SELECTED_TYPE", 0)
        countingID = intent.getIntExtra("SELECTED_ID", 0)
        binding.sayimDetayTxtSayimTur.text =
            if (mode == 0) "Kör Sayım (Listesiz)" else "Normal Sayım (Listeli)"
        checkPermission()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...") // Yükleme mesajı
        progressDialog.setCancelable(false) // Kullanıcının geri tuşuna bas
        adapter = SayimDetayListesiAdapter(this, list)
        binding.sayimDetayLvSayimEpcListesi.adapter = adapter

        viewModel.CountingEpc.observe(this) { it ->
            if (it.status == Status.LOADING) {
                showLoading();
            } else if (it.status == Status.SUCCESS) {
                if (mode == 1 && it.data!!.data.isEmpty()) {
                    isReading = false;
                } else if (mode == 1 || mode == 0) {
                    isReading = true;
                    if (it.data!!.data != null) {
                        it.data!!.data.forEach {
                            if (!list.any { ls -> ls.epc == it.epc }) {
                                list.add(it)
                            }
                        }
                        runOnUiThread {
                            adapter.notifyDataSetChanged()

                        }
                    }

                }
                // EŞEKLİK YAP BURAK
                if (mode == 1) {
                    binding.sayimDetayLblOkunanAdet.text =
                        list.count { it -> it.found == 1 }.toString()
                    binding.sayimDetayLblKalanAdet.text =
                        (list.count() - list.count { it -> it.found == 1 }).toString()
                } else {
                    binding.sayimDetayLblOkunanAdet.text =
                        list.count().toString()
                    binding.sayimDetayLblKalanAdet.text =
                        list.count().toString()
                }
                hideLoading()


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

        binding.sayimDetayBtnSenkronizeEt.setOnClickListener {
            if (yeniItemlist.any()) {
                if (mode == 0) {
                    viewModel.setCountingEpc_bilink(this,yeniItemlist, countingID.toLong())
                } else {

                }

            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("EPC BULUNAMADI")
                    .setMessage("Sayıma yeni EPC eklenmedi")
                    .setPositiveButton("Tamam") { dialog, _ ->
                        // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                        dialog.dismiss()
                    }

                    .show()
            }
        }

        viewModel.setBlinkEpc.observe(this) { it ->
            if (it.status == Status.LOADING) {
                showLoading();
            } else if (it.status == Status.SUCCESS) {

                hideLoading()
                if (it.data!!.code == "00") {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Seknorize")
                        .setMessage("Sayım Sekronize oldu")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                            dialog.dismiss()
                        }

                        .show()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Sekronize Hatası")
                        .setMessage("Sekronize Edilmedi")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            // Tamam butonuna basıldığında yapılacak işlemler buraya gelir.
                            dialog.dismiss()
                        }

                        .show()
                }
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

    private fun showLoading() {
        progressDialog.show()
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun onStart() {
        super.onStart()
        val req = com.takipsan.levinson.Entities.Retrofit.Request.CountingEpc(
            this.countingID
        )
        viewModel.getCountingEpcs(this,req);
    }

    fun onRead(v: View?) {
        if (!isOpened) {
            return
        }
        if (isReading) {
            return
        }
        // start reading 6C tags using ant 1 in cycle continuous reading mode
        isReading = UHFReader._Tag6C.GetEPC(1, 1) == 0
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

    override fun onDestroy() {
        super.onDestroy()
        UHFReader.getUHFInstance().CloseConnect();

    }

    override fun OutPutEPC(p0: EPCModel?) {
        if (p0 != null) {
            Log.d(
                TAG, " EPC: " + p0._EPC
                        + " TID: " + p0._TID
                        + " UserData:" + p0._UserData
            )
            if (isReading) {
                addEpcFromReader(p0._EPC)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            initView()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    fun addEpcFromReader(epc: String) {
        try {


        if (mode == 0) {
            synchronized(list) {
                if (!list.any { it -> it.epc == epc }) {

                    mainScope.launch {
                        try {
                            // UI işlemleri burada yapılır
                            val result = withContext(Dispatchers.Main) {
                                runOnUiThread {
                                    list.add(
                                        com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc(
                                            epc,
                                            1
                                        )
                                    )
                                    val toneGenerator =
                                        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                    adapter.notifyDataSetChanged()
                                    binding.sayimDetayLblOkunanAdet.text =
                                        list.count().toString()
                                    binding.sayimDetayLblKalanAdet.text =
                                        list.count().toString()
                                    yeniItemlist.add(
                                        com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc(
                                            epc,
                                            1
                                        )
                                    )
                                }
                            }


                        } catch (e: Exception) {
                            // Hata yönetimi
                            Log.e("UOI", e.message.toString())
                        }
                    }
                }
            }
        } else if (mode == 1) {
            synchronized(list) {


                val index = list.indexOfFirst { it -> it.epc == epc && it.found == 0 }
                if (index >= 0) {
                    mainScope.launch {
                        try {
                            // UI işlemleri burada yapılır
                            val result = withContext(Dispatchers.Default) {
                                runOnUiThread {
                                    list[index].found = 1
                                    val toneGenerator =
                                        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                    adapter.notifyDataSetChanged()
                                    binding.sayimDetayLblOkunanAdet.text =
                                        list.count { it -> it.found == 1 }.toString()
                                    binding.sayimDetayLblKalanAdet.text =
                                        (list.count() - list.count { it -> it.found == 1 }).toString()
                                    yeniItemlist.add(list[index])
                                }
                            }


                        } catch (e: Exception) {
                            // Hata yönetimi
                            Log.e("UOI", e.message.toString())
                        }
                    }
                }
            }

        }
        }catch (ex:Exception){
            Log.d("tag",ex.message.toString())
        }
    }

    companion object {
        const val REQUEST_READ_PHONE_STATE = 1
    }
}