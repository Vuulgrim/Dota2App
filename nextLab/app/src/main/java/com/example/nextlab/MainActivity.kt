package com.example.nextlab

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nextlab.Adapters.MatchAdapter
import com.example.nextlab.DataBases.HeroDB
import com.example.nextlab.DataBases.MainDB
import com.example.nextlab.Models.Hero
import com.example.nextlab.Models.Match
import com.example.nextlab.databinding.MainPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.Fragment

var matchId: String = ""
var posi: Int = 0
val heroList = mutableListOf<String>()

class MainActivity : ComponentActivity(), MatchAdapter.Listener {
    lateinit var binding: MainPageBinding
    private val adapter = MatchAdapter(this)
    private val newdateList = mutableListOf<String>()
    private val newwinnerList = mutableListOf<String>()
    private val newdurationList = mutableListOf<String>()
    private val newAvgMMRList = mutableListOf<String>()
    private val matchIDList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //инициализация БД для матчей
        val db = MainDB.getDB(this)
        //db.getDao().deleteAll()
        //инициализация БД для героев
        val heroDb = HeroDB.getDB(this)

        CoroutineScope(Dispatchers.IO).launch {
            var resivedText: String
            val client = OkHttpClient()
            val url = " https://api.opendota.com/api/heroes"
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e("Error", "No Internet")
                    runOnUiThread {
                        heroDb.getDao().selectAll().asLiveData().observe(this@MainActivity) {
                            var i = 0
                            it.forEach {
                                heroList.add(i, it.localName)
                                i++
                            }
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        runOnUiThread {
                            resivedText = response.body!!.string()
                            val recivedJSON = JSONArray(resivedText)
                            //Log.i("HeroJson", recivedJSON.length().toString())
                            for (i in 0 until recivedJSON.length()) {
                                heroList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getString("localized_name")
                                )
                                val hero = Hero(
                                    i,
                                    heroList[i]
                                )
                                Thread {
                                    //heroDb.getDao().deleteAll()
                                    heroDb.getDao().InsertHero(hero)
                                }.start()
                            }
                        }
                    }
                }
            })
        }


        CoroutineScope(Dispatchers.IO).launch {
            var resivedText: String
            val client = OkHttpClient()
            val url = "https://api.opendota.com/api/publicMatches"
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e("ERROR", "NO INTERNET")
                    runOnUiThread {
                        db.getDao().getAllMatches().asLiveData().observe(this@MainActivity) {
                            var i = 0;
                            it.forEach {
                                matchIDList.add(i, it.matchId)
                                newdateList.add(i, it.date)
                                newwinnerList.add(i, it.winner)
                                newdurationList.add(i, it.durationOfMatch)
                                newAvgMMRList.add(i, it.averageMMR)
                                i++
                            }
                            init()
                        }
                    }
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        runOnUiThread {
                            resivedText = response.body!!.string()
                            //получаю JSONArray
                            val recivedJSON = JSONArray(resivedText)
                            for (i in 0 until recivedJSON.length()) {
                                matchIDList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getLong("match_id")
                                        .toString()
                                )
                                //Log.i("MatchID", matchIDList[i] + "_" + i)
                                newdateList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getLong("start_time")
                                        .toString()
                                )
                                newdurationList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getLong("duration").toString()
                                )
                                newwinnerList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getBoolean("radiant_win")
                                        .toString()
                                )
                                newAvgMMRList.add(
                                    i,
                                    recivedJSON.getJSONObject(i).getLong("avg_rank_tier")
                                        .toString()
                                )
                                val match = Match(
                                    matchIDList[i],
                                    newdateList[i],
                                    newwinnerList[i],
                                    newdurationList[i],
                                    newAvgMMRList[i]
                                )
                                Thread {
                                    db.getDao().insertMatch(match)
                                }.start()
                            }
                            init()
                        }
                    }
                }
            })
        }
    }

    //Функция которая конвертирует наши данные в читаемые
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToReadeable() {
        for (i in 0 until newdateList.size) {
            //Часть кода для даты-----------------------------------------
            val UNIXdate = newdateList[i]
            val date = Instant.ofEpochSecond(UNIXdate.toLong()).atZone(ZoneId.systemDefault())
                .toLocalDate()
            newdateList[i] = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            //Часть кода для победителя-----------------------------------
            if (newwinnerList[i] == "true") {
                newwinnerList[i] = "Radiant"
            } else {
                newwinnerList[i] = "Dire"
            }
            //Часть кода для продолжения игры-----------------------------
            newdurationList[i] = TimeUnit.SECONDS.toMinutes(newdurationList[i].toLong()).toString()
            //Часть кода для ранга игры-----------------------------------
            when (newAvgMMRList[i].toInt()) {
                in 10..15 -> newAvgMMRList[i] = "Herald"
                in 20..25 -> newAvgMMRList[i] = "Guardian"
                in 30..35 -> newAvgMMRList[i] = "Crusader"
                in 40..45 -> newAvgMMRList[i] = "Archon"
                in 50..55 -> newAvgMMRList[i] = "Legend"
                in 60..65 -> newAvgMMRList[i] = "Ancient"
                in 70..75 -> newAvgMMRList[i] = "Divine"
                in 80..85 -> newAvgMMRList[i] = "Immortal"
                else -> newAvgMMRList[i] = "Nothing"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        convertToReadeable()
        binding.apply {
            //binding.rcView
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
            var index = 0
            //Log.i("Test1", newdateList.size.toString())
            while (index < newdateList.size) {
                val match = Match(
                    matchIDList[index],
                    newdateList[index],
                    newwinnerList[index],
                    newdurationList[index],
                    newAvgMMRList[index]
                )
                //Log.i("Test", newdateList[index])
                adapter.addMatch(match)
                index++
            }
        }
    }


    override fun onClick(match: Match, pos: Int) {
        startActivity(Intent(this, secondPage::class.java))
        //сделать так, чтобы posi являлся id матча, т.к мы передаём это на вторую страницу
        posi = pos
        matchId = matchIDList[pos]
        Log.w("NUmber", matchIDList[pos])
        //Log.w("First", pos.toString())
    }
}