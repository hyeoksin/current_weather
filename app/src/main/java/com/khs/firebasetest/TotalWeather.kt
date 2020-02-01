package com.khs.firebasetest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// value와 매칭하려면 Serializable 을 반드시 명시해야 함
class TotalWeather(
    var main:Main?=null,
    @SerializedName("weather") var weatherList:ArrayList<Weather>?=null
):Serializable

class Weather(
    var description:String?=null,
    var icon:String?=null,
    var main:String?=null
):Serializable

class Main(
    var humidity:Int?=null,
    var pressure:Int?=null,
    var temp:Float?=null,
    @SerializedName("temp_max")  var tempMax:Float?=null,
    @SerializedName("temp_min")  var tempMin:Float?=null
):Serializable

