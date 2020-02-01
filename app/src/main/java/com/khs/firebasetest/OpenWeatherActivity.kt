package com.khs.firebasetest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.activity_open_weather.*
import retrofit2.*

class OpenWeatherActivity : AppCompatActivity(),LocationListener{

    private val PERMISSION_REQUEST_CODE = 2000
    private val APP_ID = "43d83f49060f0cf0b95204408fd076de"
    private val UNITS = "metric"
    private val LANGUAGE= "kr"

    private val WEATHER_ICON_URL = "https://openweathermap.org/img/wn/"
    // https로 요청을 해야한다.

    private lateinit var backPressHolder: OnBackPressHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_weather)

        backPressHolder = OnBackPressHolder()
        getLocationInfo()

        setting.setOnClickListener {
            startActivity((Intent(this,AccountSettingActivity::class.java)))
        }
    }

    // 뒤로가기 버튼을 눌렀을경우
    override fun onBackPressed() {
        backPressHolder.onBackPressed()
    }

    private fun getLocationInfo(){
        // 23이상 버전에서 위치권한 동의를 받아야 한다.
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this@OpenWeatherActivity,
                 android.Manifest.permission.ACCESS_FINE_LOCATION
            )!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this@OpenWeatherActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
        }else{
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location!=null){
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("DEBUG","latitude: "+latitude)
                Log.d("DEBUG","longitude: "+longitude)
                requestWeatherInfoOfLocation(latitude = latitude,longitude = longitude)
            }else{
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0F,
                    this
                )
                locationManager.removeUpdates(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK) getLocationInfo()
        }
    }

    private fun requestWeatherInfoOfLocation(latitude:Double,longitude:Double){
        (application as WeatherApplication)
            .requestAppService()
            ?.getWeatherInfoOfCoordinates(
                latitude = latitude,
                longitude = longitude,
                appID = APP_ID,
                units = UNITS,
                language = LANGUAGE
            )?.enqueue(object : Callback<TotalWeather> {
                override fun onFailure(call: Call<TotalWeather>, t: Throwable) {
                    loading_text.text = "데이터를 불러오는데 실패했습니다."
                }

                override fun onResponse(
                    call: Call<TotalWeather>,
                    response: Response<TotalWeather>
                ) {
                    if(response.isSuccessful){
                        val totalWeather = response.body()
                        totalWeather?.let {
                            drawCurrentWeather(totalWeather)
                        }
                        Log.d("DEBUG","weather: "+totalWeather?.main?.temp)
                    }else{
                        loading_text.text ="데이터를 불러오는데 실패했습니다."
                    }
                }
            })
    }

    private fun drawCurrentWeather(currentWeather:TotalWeather){
        with(currentWeather){

            // http://openweathermap.org/img/wn/10d@2x.png
            var icon = this.weatherList?.getOrNull(0)?.icon
            Log.d("DEBUG","icon: "+icon)

            this.weatherList?.getOrNull(0)?.let{
                // get(0)이 null일때 에러가 발생하지 않는다.
                // 뒷부분을 실행하지 않음
                it.icon?.let {
                    val glide = Glide.with(this@OpenWeatherActivity)
                    glide.load(WEATHER_ICON_URL+it+"@2x.png")
                        .into(current_icon)
                }

                it.main?.let { current_name.text =it }
                it.description?.let{ current_description.text = it }

            }

            this.main?.temp?.let    { current_now.text = String.format("%.1f",it) }
            this.main?.tempMax?.let { current_max.text = String.format("%.1f",it)}
            this.main?.tempMin?.let { current_min.text = String.format("%.1f",it)}

            loading_view.visibility = View.GONE
            weather_view.visibility = View.VISIBLE
        }
    }

    override fun onLocationChanged(location: Location?) {
        val latitude = location?.latitude
        val longitude = location?.longitude
        if(latitude!=null && longitude!=null){
            requestWeatherInfoOfLocation(latitude = latitude,longitude = longitude)
        }
        Log.d("DEBUG","onLocationChanged() latitude: "+latitude)
        Log.d("DEBUG","onLocationChanged() logitude: "+longitude)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    inner class OnBackPressHolder(
    ){
        private var backPressHolder:Long=0

        fun onBackPressed(){
            if(System.currentTimeMillis()> backPressHolder +2000){
                backPressHolder = System.currentTimeMillis()
                showBackToast()
                return
            }

            if(System.currentTimeMillis() <=backPressHolder +2000){
                finishAffinity()
            }
        }

        fun showBackToast(){
            Toast.makeText(this@OpenWeatherActivity,"한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show()
        }
    }


}
