package com.example.nextlab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nextlab.Adapters.MatchDetailsAdapter
import com.example.nextlab.DataBases.matchdb
import com.example.nextlab.Models.MatchDetails
import com.example.nextlab.databinding.SecondPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Thread.sleep

var id = 0
class secondPage : ComponentActivity() {
    lateinit var binding: SecondPageBinding
    private val secondPageAdapter = MatchDetailsAdapter()

    private val newPlayerList = mutableListOf<String>()
    private val heroOfPlayer = mutableListOf<String>()
    private val newNetworthList = mutableListOf<String>()
    var isInetOn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SecondPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Инициализация БД для подробности матча
        val matchDetailsdb = matchdb.getDB(this)
        //чистил Бд по мере выполнения лабораторной
        //matchDetailsdb.getDao().deleteAll()
        matchDetailsdb.getDao().getMaxId().asLiveData().observe(this) {
            it.forEach{
                id = it.id
            }
         }
        var recivedText: String
        val client = OkHttpClient()
        val url = "https://api.opendota.com/api/matches/" + matchId
        Log.i("URL", url)
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    e.printStackTrace()
                    Log.i("Error", "No Internet")
                    isInetOn = false
                    runOnUiThread {
                        matchDetailsdb.getDao().selectById(matchId).asLiveData()
                            .observe(this@secondPage) {
                                var i = 0
                                it.forEach {
                                    newPlayerList.add(i, it.player_name)
                                    heroOfPlayer.add(i, it.hero)
                                    newNetworthList.add(i, it.networth)
                                    i++
                                }
                                init()
                                Log.i("I", it.toString())
                            }
                    }
                }
            }
            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (response.isSuccessful) {
                        recivedText = response.body!!.string()
                        //Log.i("MatchTest", recivedText)
                        val recivedJSON = JSONObject(recivedText)
                        //Log.i("JSONFull", recivedJSON.toString())
                        //var test1 = recivedJSON.getJSONArray("players").getString(0)
                        //Log.i("JSON1", test1.toString())
                        id++
                        for (i in 0 until recivedJSON.getJSONArray("players").length()) {
                            if (recivedJSON.getJSONArray("players").getJSONObject(i)
                                    .has("personaname")
                            ) {
                                newPlayerList.add(
                                    i, recivedJSON.getJSONArray("players").getJSONObject(i)
                                        .getString("personaname")
                                )
                            } else {
                                newPlayerList.add(i, "Unknown")
                            }

                            heroOfPlayer.add(i, recivedJSON.getJSONArray("players").getJSONObject(i)
                                .getString("hero_id"))

                            newNetworthList.add(
                                i,
                                recivedJSON.getJSONArray("players").getJSONObject(i)
                                    .getString("net_worth")
                            )
                            Log.i("ID + 1", id.toString())
                            val matchDetails = MatchDetails (
                                id,
                                matchId,
                                newPlayerList[i],
                                heroOfPlayer[i],
                                newNetworthList[i]
                            )
                            Log.i("MatchDetails", matchDetails.toString())
                            Thread {
                                matchDetailsdb.getDao().insertDetails(matchDetails)
                            }.start()
                            id++
                        }
                    }
                    init()
                }
            }
        })


        //Кнопочка на главный экран
        val button = findViewById<ImageButton>(R.id.toMainButton)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun init() {
        binding.apply {
            rcSecond.layoutManager = LinearLayoutManager(this@secondPage)
            rcSecond.adapter = secondPageAdapter


            var index = 0
            //нужно брать id прямиком из бд и инкрементировать его
            if(isInetOn) {
                id -= 10
            }
            while (index < 10) {
                //Log.d("Test", posi.toString())
                val gamedetail = MatchDetails(
                    id,
                    matchId,
                    newPlayerList[index],
                    heroList[heroOfPlayer[index].toInt()],
                    newNetworthList[index]
                )
                Log.i("MatchDetails", gamedetail.toString())
                //println(gamedetail)
                secondPageAdapter.adddetails(gamedetail)
                id++
                index++
            }
        }
    }
}