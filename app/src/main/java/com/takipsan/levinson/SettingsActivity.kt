package com.takipsan.levinson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.takipsan.levinson.Entities.Retrofit.Status
import com.takipsan.levinson.R
import com.takipsan.levinson.alert.MLoadingAlerts
import com.takipsan.levinson.databinding.ActivityMainBinding
import com.takipsan.levinson.databinding.ActivitySettingsBinding
import com.takipsan.levinson.viewModel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel  by viewModels()
    private lateinit var fabloading: MLoadingAlerts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewModal = viewModel
        fabloading =  MLoadingAlerts(this,"FABRİKA AYARLARI KAYDEDİLİYOR")

        viewModel.factoryResetState.observe(this) {
            if (it.status == Status.LOADING) {
                fabloading.goster()
            } else if (it.status == Status.SUCCESS) {
                fabloading.kapat()
                if (it.data!! == 0){
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Başarılı")
                        .setMessage("Cihazın fabrika ayarlarına başarıyla dönüldü.")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Uyarı")
                        .setMessage("Cihazın fabrika ayarlarına dönülmedi. Lütfen cihazı sıfırlayın.")
                        .setPositiveButton("Tamam") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

            } else if (it.status == Status.FORBIDDEN) {
                fabloading.kapat()
                MaterialAlertDialogBuilder(this)
                    .setTitle("Uyarı")
                    .setMessage("RFID okuyucuya bağlanılamadı. Lütfen cihaz bağlantısını kontrol edin.")
                    .setPositiveButton("Tamam") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        binding.antennaPower.addOnChangeListener { slider, value, fromUser ->
            viewModel.sliderValueChanged(value)
        }

        binding.BaseBand.setOnItemClickListener { parent, _, position, _ ->
            viewModel.basebandPostion(position)
        }
        binding.basebandSpeed.setOnItemClickListener { parent, _, position, _ ->
            viewModel.basebandSpeedPostion(position)
        }
        binding.session.setOnItemClickListener { parent, _, position, _ ->
            viewModel.sessionPosition(position)
        }
        binding.QValue.setOnItemClickListener { parent, _, position, _ ->
            viewModel.qvaluePosition(position)
        }
        binding.flag.setOnItemClickListener { parent, _, position, _ ->
            viewModel.FlagPosition(position)
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.setUI(this)

    }


}