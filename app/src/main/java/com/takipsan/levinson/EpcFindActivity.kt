package com.takipsan.levinson

import android.Manifest
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
import com.pda.rfid.EPCModel
import com.pda.rfid.IAsynchronousMessage
import com.pda.rfid.uhf.UHFReader
import com.port.Adapt
import com.takipsan.levinson.databinding.ActivityEpcFindBinding
import com.takipsan.levinson.databinding.ActivitySearchingBinding
import com.takipsan.levinson.viewModel.UrunAramaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class EpcFindActivity : AppCompatActivity(), IAsynchronousMessage {
    private lateinit var binding: ActivityEpcFindBinding
    private lateinit var myEpc: String
    private val viewModel: UrunAramaViewModel by viewModels()
    private val TAG = "EPCFIND"
    private var isOpened: Boolean = false;
    private var isReading: Boolean = false
    var toneGenerator: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_epc_find)
        binding.epcBulLblEpc.text = intent.getStringExtra("epc") ?: ""
        myEpc = binding.epcBulLblEpc.text.toString()
        binding.epcBulLblName.text = intent.getStringExtra("name") ?: ""
        binding.epcBulLblKeyword.text = intent.getStringExtra("code") ?: ""
        binding.viewModel = viewModel
        checkPermission()

        viewModel.rssi.observe(this) { value ->
            // LiveData'dan gelen değeri ProgressBar'un progress özelliğine dönüştür
            val progressValue = 100+ (value.toInt()) // Örnek olarak 0-100 arasında bir dönüşüm

            // ProgressBar'un progress özelliğine dönüştürülmüş değeri atama
            binding.epcBulProgressBar.progress = progressValue
            Log.d("ES",progressValue.toString())
        }

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
            Log.d(TAG, p0.RSSI().toString())
            viewModel.putRssi(p0.RSSI())
            toneGenerator?.release()
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            when {
                p0.RSSI() >= -50 -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                }
                p0.RSSI() >= -70 -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ANSWER, 2000)
                }
                p0.RSSI() >= -90 -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 2000)
                }
                else -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 2000)
                }
            }

        } else {

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
        isReading = UHFReader._Tag6C.GetEPC_MatchEPC(1, 1,myEpc) == 0
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
            binding.epcBulBtnAra.setText("Aranıyor...")

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
            viewModel.putRssi(0.0)
            binding.epcBulBtnAra.setText("Aramaya Başla")

        }
        return super.onKeyUp(keyCode, event)
    }


}