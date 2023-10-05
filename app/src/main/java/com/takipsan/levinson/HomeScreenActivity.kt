package com.takipsan.levinson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.takipsan.levinson.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen)

        binding.fMainBtnSayim.setOnClickListener{
            val intent = Intent(this,CountingListActivity::class.java)
            startActivity(intent)
        }

        binding.fMainBtnCihazAyarlari.setOnClickListener{
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.fMainBtnUrunBul.setOnClickListener{
            val intent = Intent(this,SearchingActivity::class.java)
            startActivity(intent)
        }
        binding.fMainBtnSevkiyat.setOnClickListener{
            val intent = Intent(this,SevkiyatActivity::class.java)
            startActivity(intent)
        }
    }
}