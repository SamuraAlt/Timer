package com.example.altov.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.altov.timer.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext,TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        binding.buttonStartStop.setOnClickListener { startStopTimer() }
        binding.buttonReset.setOnClickListener { resetTimer() }

        }
    private val updateTime:BroadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIMER_EXTRA,0.0)
            binding.title.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return timeString(hours,minutes,seconds)
    }

    private fun timeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d",hour,min,sec)

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.title.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if(timerStarted){
            stopTimer()
        }
        else startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIMER_EXTRA,time)
        startService(serviceIntent)
        binding.buttonStartStop.text = getString(R.string.stop)
        binding.buttonStartStop.icon = getDrawable(R.drawable.baseline_pause_circle_24)
        timerStarted = true
    }

    private fun stopTimer() {

        stopService(serviceIntent)
        binding.buttonStartStop.text = getString(R.string.start)
        binding.buttonStartStop.icon = getDrawable(R.drawable.baseline_play_arrow_24)
        timerStarted = false
    }
}

