package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mapplaces.database.DataBaseHandler
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherService
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Integer.min
import java.lang.Math.max
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var daysFromCurrent: Int = 0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var lastLongitude = 0.0
    private var lastLatitude = 0.0

    private var mProgressDialog: Dialog? = null

    var list_of_items = arrayListOf("Холм-Жирковский район", "Item 2", "Item 3")

    private lateinit var currentTimeCalendar: Calendar

    private lateinit var mSharedPreferences: SharedPreferences

    override fun onResume() {
        currentTimeCalendar = Calendar.getInstance()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        showSelectedDate(currentTimeCalendar)
        val bh = DataBaseHandler(this)
        list_of_items = bh.getPlacesList()

        val aa = ArrayAdapter(this, R.layout.row,R.id.weekofday, list_of_items)
        spinnerLocationWeather!!.adapter = aa

        btnIncreaseDate.setOnClickListener {
            changeDate(Constants.NEXT_DAY)
        }
        btnDecreaseDate.setOnClickListener {
            changeDate(Constants.PREVIOUS_DAY)
        }

        setUpUI(Constants.CURRENT_DAY)

        if (isLocationEnabled()) {
            checkPermissions()
        } else {
            showLocationSettings()
        }
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onResume()

        spinnerLocationWeather.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val db = DataBaseHandler(this@MainActivity)
                val (longitude, latitude) = db.getLongitudeLatitude(list_of_items[position])

                if(longitude.toDouble() == 0.0 && latitude.toDouble() == 0.0){
                        setWeatherIfVisitedToday(lastLongitude,lastLatitude)
                }else {
                    if(!setWeatherIfVisitedToday(longitude.toDouble(),latitude.toDouble())){
                        getLocationWeatherDetails(latitude.toDouble(),longitude.toDouble())
                    }
                }
                daysFromCurrent = Constants.CURRENT_DAY
                changeDate(daysFromCurrent)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


    }

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    private fun checkPermissions(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    requestLocationData()

                }
                if(report.isAnyPermissionPermanentlyDenied){
                    Toast.makeText(this@MainActivity, "You denied a permission", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    private fun showLocationSettings(){
        AlertDialog.Builder(this).setMessage("Please turn on location settings")
            .setPositiveButton("Go to settings"){
                    _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                    dialog,_ ->
                dialog.dismiss()
            }.show()
    }
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("You turned off permissions. It can be enabled by app settings")
            .setPositiveButton("Go to settings"){
                _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            val latitude = mLastLocation.latitude
            val longitude = mLastLocation.longitude


            lastLatitude = BigDecimal(latitude).setScale(3, RoundingMode.DOWN).toDouble()
            lastLongitude = BigDecimal(longitude).setScale(3, RoundingMode.DOWN).toDouble()

            if (!setWeatherIfVisitedToday(lastLongitude, lastLatitude)) {
                getLocationWeatherDetails(lastLatitude, lastLongitude)
            }

        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double){
        if(Constants.isNetWorkAvailable(this@MainActivity)){

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(latitude, longitude, true, Constants.APP_ID)
            Log.i("Longitude:", "$longitude")
            Log.i("Latitude:", "$latitude")

            showCustomProgressDialog()

            listCall.enqueue(object: Callback<WeatherResponse>{

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    hideProgressDialog()
                    if(response.isSuccessful){
                        makeToast("Данные о погоде обновлены")
                        val weatherList: WeatherResponse? = response.body()
                        val dbHandler = DataBaseHandler(this@MainActivity)
                        dbHandler.addPlace(weatherList!!.geo_object.locality.name!!, longitude.toString(), latitude.toString())
                        saveWeatherData(weatherList)

                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()


                        setUpUI(Constants.CURRENT_DAY)
                    }else{
                        var rc = response.code()
                        when(rc){
                            400 -> Log.e("Error 400", "Bad connection")
                            404 -> Log.e("Error 404", "Not found")
                            else -> {
                                Log.e("Error:","code of the error: $rc")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressDialog()
                    makeToast("Не удалось получить данные о погоде")
                    Log.e("ERRROooRR", t.message.toString())
                }

            })
        } else{
            makeToast("Отсутствует интернет соединения, будет показана последняя доступная информация")
           // showLastData()
        }
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog!!.dismiss()
        }
    }

    private fun makeToast(str: String){
        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
    }

    private fun setUpUI(displayDay: Int){
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA,"")
        if(!weatherResponseJsonString.isNullOrEmpty()){
            if(displayDay != 0) tvTempNow.text = "В ${currentTimeCalendar.get(Calendar.HOUR_OF_DAY)}:00"
            else tvTempNow.text = "сейчас"
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            tv_country.text = weatherList.geo_object.country.name
            tv_name.text = weatherList.geo_object.locality.name
            tv_sunrise_time.text = weatherList.forecasts[displayDay].sunrise
            tv_sunset_time.text = weatherList.forecasts[displayDay].sunset
            tv_speed.text = weatherList.forecasts[displayDay].hours[currentTimeCalendar.get(Calendar.HOUR_OF_DAY)].wind_speed.toString()
            tv_min.text = weatherList.forecasts[displayDay].hours[Constants.NIGHT_HOUR].temp.toString() + "°C"
            tv_max.text = weatherList.forecasts[displayDay].hours[Constants.DAY_HOUR].temp.toString() + "°C"
            tv_temp.text = weatherList.forecasts[displayDay].hours[currentTimeCalendar.get(Calendar.HOUR_OF_DAY)].temp.toString() + "°C"
            tv_humidity.text = weatherList.forecasts[displayDay].hours[currentTimeCalendar.get(Calendar.HOUR_OF_DAY)].humidity.toString()
            val conditions = weatherList.forecasts[displayDay].hours[currentTimeCalendar.get(Calendar.HOUR_OF_DAY)].condition!!.split("-and-")
            var weatherDiscription: String = ""
            for(i in conditions){
                weatherDiscription += Constants.conditions[i] + " "
                tv_main_description.text = weatherDiscription
            }
            val iconUrl = "https://yastatic.net/weather/i/icons/funky/dark/${ weatherList.forecasts[displayDay].hours[currentTimeCalendar.get(Calendar.HOUR_OF_DAY)].icon}.svg"

            GlideToVectorYou.justLoadImage(this@MainActivity, Uri.parse(iconUrl), iv_main)

        }else  tv_country.text = "Нет данных"
    }


    private fun showSelectedDate(displayCalendar: Calendar){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        tvCurrentDate.text = sdf.format(displayCalendar.time).toString()
    }

    private fun changeDate(value: Int){
        var displayCalendar: Calendar = Calendar.getInstance()
        daysFromCurrent += value
        daysFromCurrent = min(daysFromCurrent,2)
        daysFromCurrent = daysFromCurrent.coerceAtLeast(0)

        when(daysFromCurrent) {
            2 -> {
                btnIncreaseDate.setBackgroundResource(R.drawable.change_date_button_background_disabled)
                btnDecreaseDate.setBackgroundResource(R.drawable.change_date_button_background)
            }
            0 -> {
                btnDecreaseDate.setBackgroundResource(R.drawable.change_date_button_background_disabled)
                btnIncreaseDate.setBackgroundResource(R.drawable.change_date_button_background)
            }
            else -> {
                btnDecreaseDate.setBackgroundResource(R.drawable.change_date_button_background)
                btnIncreaseDate.setBackgroundResource(R.drawable.change_date_button_background)
            }
        }
        Log.i("valueOFdays", "$daysFromCurrent")
        displayCalendar.add(Calendar.DAY_OF_MONTH,daysFromCurrent)
        showSelectedDate(displayCalendar)
        setUpUI(daysFromCurrent)
    }

    private fun setWeatherIfVisitedToday(longitude: Double, latitude: Double): Boolean{
        val db = DataBaseHandler(this@MainActivity)
        val place = db.getPlace(longitude.toString(),latitude.toString())
        val weather = db.getWeatherToday(place)
        if(!weather.isNullOrEmpty()){
            val editor = mSharedPreferences.edit()
            editor.putString(Constants.WEATHER_RESPONSE_DATA, weather)
            editor.apply()
            setUpUI(Constants.CURRENT_DAY)
            return true
        }
        return false
    }

    private fun saveWeatherData(weather: WeatherResponse?){
        Log.i("SetData", "started")
        val db = DataBaseHandler(this@MainActivity)
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        db.addWeather(weather!!.geo_object.locality.name!!,sdf.format(currentTimeCalendar.time).toString(),Gson().toJson(weather))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when(item.itemId){
            R.id.action_refresh -> {
                updateWeatherInfo()
                true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun updateWeatherInfo(){
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA,"")
        val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
        getLocationWeatherDetails(BigDecimal(weatherList.info.lat).setScale(3, RoundingMode.DOWN).toDouble(),
            BigDecimal(weatherList.info.lon).setScale(3, RoundingMode.DOWN).toDouble())

    }


}