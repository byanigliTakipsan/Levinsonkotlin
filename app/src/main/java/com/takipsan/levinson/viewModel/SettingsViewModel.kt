package com.takipsan.levinson.viewModel

import android.content.Context
import android.se.omapi.Session
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pda.rfid.EPCModel
import com.pda.rfid.IAsynchronousMessage
import com.pda.rfid.hf.HF
import com.pda.rfid.uhf.UHF
import com.pda.rfid.uhf.UHFReader
import com.port.Adapt
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.Login
import com.takipsan.levinson.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class SettingsViewModel @Inject constructor(private val apiRepository: ApiRepository) :
    ViewModel(), IAsynchronousMessage {

    private val _factoryResetState = MutableLiveData<Resource<Int>>()
    var factoryResetState: MutableLiveData<Resource<Int>> = _factoryResetState


    private val _antennaPower = MutableLiveData<Float>(0.0f)
    val antennaPower: LiveData<Float> get() = _antennaPower

    private val _basebandValue = MutableLiveData<Int>(0)
    val basebandValue: LiveData<Int> get() = _basebandValue
    private val _basebandSpeedValue = MutableLiveData<Int>(0)
    val basebandSpeedValue: LiveData<Int> get() = _basebandSpeedValue
    private val _SessionValue = MutableLiveData<Int>(0)
    val SessionValue: LiveData<Int> get() = _SessionValue
    private val _QValue = MutableLiveData<Int>(0)
    val Qvalue: LiveData<Int> get() = _QValue
    private val _FlagValue = MutableLiveData<Int>(0)
    val FlagValue: LiveData<Int> get() = _FlagValue

    var isOpened = false
    lateinit var gradVersion: String;
    lateinit var DeviceVersion: String;


    lateinit var baseBandString: String
    lateinit var BaseBandSpeed: String
    lateinit var SessionString: String
    lateinit var QValueString: String
    lateinit var FlagString: String


    val baseBandList: ArrayList<baseBand> = ArrayList()
    val sessionList = ArrayList<Int>()
    val QValueList = ArrayList<Int>()
    val flagList = ArrayList<Flag>()
    val rateList = ArrayList<Rate>()

    init {
        baseBandList.add(baseBand(0, "GB_920_to_925MHz"))
        baseBandList.add(baseBand(1, "GB_840_to_845MHz"))
        baseBandList.add(baseBand(2, "GB_920_to_925MHz_and_GB_840_to_845MHz"))
        baseBandList.add(baseBand(3, "FCC_902_to_928MHz"))
        baseBandList.add(baseBand(4, "ETSI_866_to_868MHz"))

        flagList.add(Flag(0, "Flag A only"))
        flagList.add(Flag(1, "Flag B only"))
        flagList.add(Flag(2, "Flag A & Flag B"))

        rateList.add(Rate(0, "0|Tari=25us, FM0, LHF=40KHz"))
        rateList.add(Rate(1, "1|Dense"))
        rateList.add(Rate(2, "2|Tari=25us, Miller4, BLF=300KHz）"))
        rateList.add(Rate(3, "3|Fast"))
        rateList.add(Rate(4, "4|Tari=20us, Miller4, BLF=320KHz）"))
        rateList.add(Rate(5, "5|Tari=6.25us, Miller2, BLF=320KHz）"))
        rateList.add(Rate(6, "6|Tari=12.5us, FM0, BLF=640KHz）"))
        rateList.add(Rate(7, "7|Tari=7.5us, FM0, BLF=80KHz）"))
        rateList.add(Rate(8, "8|Tari=7.5us, Miller2, BLF=640KHz）"))
        rateList.add(Rate(9, "9|Tari=7.5us, Miller4, BLF=640KHz）"))
        rateList.add(Rate(10, "10|Tari=15us, Miller2, BLF=320KHz）"))
        rateList.add(Rate(11, "11|Tari=20us, Miller2, BLF=320KHz）"))
        rateList.add(Rate(12, "12|Tari=20us, Miller4, BLF=250KHz）"))
        rateList.add(Rate(13, "13|Tari=20us, Miller8, BLF=160KHz）"))
        rateList.add(Rate(255, "255|AUTO"))

        for (i in 0..3) {
            sessionList.add(i)
        }
        for (i in 0..15) {
            QValueList.add(i)
        }
    }


    fun setUI(context: Context) {
        Adapt.init(context)
        isOpened = UHFReader.getUHFInstance().OpenConnect(this)
        if (isOpened) {
            gradVersion = "2.0.3"
            _antennaPower.value = UHFReader._Config.GetANTPowerParam().toFloat()
            DeviceVersion = UHFReader._Config.GetReaderBaseBandSoftVersion()
            val localBaseBandValue = UHFReader._Config.GetFrequency()

            baseBandString = baseBandList[localBaseBandValue].toString()
            _basebandValue.value = localBaseBandValue

            val resualtSetSetting = UHFReader._Config.GetEPCBaseBandParam().split('|')
            _basebandValue.value = resualtSetSetting[0].toInt()
            BaseBandSpeed =
                rateList.filter { it -> it.id == resualtSetSetting[0].toInt() }.first().toString()
            _SessionValue.value = resualtSetSetting[2].toInt()
            SessionString = resualtSetSetting[2]
            _QValue.value = resualtSetSetting[1].toInt()
            QValueString = resualtSetSetting[1]
            _FlagValue.value = resualtSetSetting[3].toInt()
            FlagString = flagList[resualtSetSetting[3].toInt()].toString()

        }
        UHFReader.getUHFInstance().CloseConnect()
    }

    fun sliderValueChanged(value: Float) {
        _antennaPower.value = value
    }

    fun basebandPostion(position:Int){
        _basebandValue.value = baseBandList[position].id
    }

    fun basebandSpeedPostion(position: Int){
        _basebandSpeedValue.value = rateList[position].id
    }
    fun sessionPosition(position: Int){
        _SessionValue.value = sessionList[position]
    }
    fun  qvaluePosition(position: Int){
        _QValue.value = QValueList[position]
    }
    fun FlagPosition(position:Int){
        _FlagValue.value = flagList[position].id
    }

    fun factoryReset(context: Context){
        _factoryResetState.postValue(Resource.loading(null))
        Adapt.init(context)
        isOpened = UHFReader.getUHFInstance().OpenConnect(this)
        if (isOpened){
           val a = UHFReader._Config.SetReaderRestoreFactory();
            _factoryResetState.postValue(Resource.success(a))
        }
        else{
            _factoryResetState.postValue(Resource.forbidden(null))
        }
        UHFReader.getUHFInstance().CloseConnect()
        setUI(context)

    }

    fun saveSettings(context: Context){
        _factoryResetState.postValue(Resource.loading(null))
        Adapt.init(context)
        isOpened = UHFReader.getUHFInstance().OpenConnect(this)
        if (isOpened){
            val a = UHFReader._Config.SetANTPowerParam(1,antennaPower.value!!.toInt());
            val b = UHFReader._Config.SetFrequency(basebandValue.value!!)
            val c = UHFReader._Config.SetEPCBaseBandParam(basebandSpeedValue.value!!,Qvalue.value!!,SessionValue.value!!,FlagValue.value!!)
            _factoryResetState.postValue(Resource.success(a+b+c))
        }
        else{
            _factoryResetState.postValue(Resource.forbidden(null))
        }
        UHFReader.getUHFInstance().CloseConnect()
        setUI(context)
    }


    fun getBaseBandAdapter(context: Context): ArrayAdapter<baseBand> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, baseBandList)
    }

    fun getSessionAdapter(context: Context): ArrayAdapter<Int> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, sessionList)
    }

    fun getQvalueAdapter(context: Context): ArrayAdapter<Int> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, QValueList)
    }

    fun getRateAdapter(context: Context): ArrayAdapter<Rate> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, rateList)
    }

    fun getFlagAdapter(context: Context): ArrayAdapter<Flag> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, flagList)
    }

    override fun OutPutEPC(p0: EPCModel?) {
        TODO("Not yet implemented")
    }
}

class baseBand(
    var id: Int,
    var text: String

) : Serializable {
    override fun toString(): String {
        return text ?: ""
    }
}

class Flag(
    var id: Int,
    var text: String

) : Serializable {
    override fun toString(): String {
        return text ?: ""
    }
}

class Rate(
    var id: Int,
    var text: String

) : Serializable {
    override fun toString(): String {
        return text ?: ""
    }
}
