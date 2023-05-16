package com.sk.superlock.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityNetworkAnalysisBinding
import java.util.*

class NetworkAnalysisActivity : BaseActivity() {

    private lateinit var binding: ActivityNetworkAnalysisBinding

    companion object {
        const val TAG = "NetworkAnalysisActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbarNetworkAnalysisActivity)

        binding.chartUpload.init(intArrayOf(5, 30, 100, 65, 80))
        binding.chartDownload.init(intArrayOf(5, 30, 65, 50, 100))

        binding.btnCheckNetworkStatus.setOnClickListener {
            val handler = Handler(Looper.getMainLooper())
            // create a Runnable to update the download and upload speed every second.
            val runnable = object : Runnable {
                override fun run() {
                    // get the current download and upload speed.
                    val downloadSpeed = getDownloadSpeed()
                    val uploadSpeed = getUploadSpeed()

                    // set the text of the tvDownload and tvUpload text views.
                    binding.tvDownload.text = buildString {
                        append(downloadSpeed)
                        append(" MB/s")
                    }
                    binding.tvUpload.text = buildString {
                        append(uploadSpeed)
                        append(" MB/s")
                    }
                    // post the runnable again to update the download and upload speed every second.
                    handler.postDelayed(this, 1000)
                }
            }
            // post the runnable to the Handler.
            handler.post(runnable)

            val timer = Timer()
            timer.schedule(object: TimerTask(){
                override fun run() {
                    handler.removeCallbacks(runnable)

//                    val networkQuality = checkNetworkQuality()
                }
            }, 30000)
        }
    }

   /* fun checkNetworkQuality(): String {
        val downloadSpeed = getDownloadSpeed()
        val uploadSpeed = getUploadSpeed()
        val latency = getLatency()
        val signalStrength = getSignalStrength()
        var networkQuality = "Good"

        if (downloadSpeed < 10 && uploadSpeed < 10) {
            networkQuality = "Bad"
        }
        if (latency > 100) {
            networkQuality = "Bad"
        }
        if (signalStrength < 50) {
            networkQuality = "Bad"
        }

        return networkQuality
    }

    fun getLatency(): Int{
        val pingResult = ping("8.8.8.8", 5)
        return pingResult.rtt.toInt()
    }

    fun getSignalStrength(): Int{
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.signalStrength.get(TelephonyManager.SIGNAL_STRENGTH_INDEX)
    }*/

    private fun getNetworkCapabilities(): NetworkCapabilities? {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    }

    // download speed
    private fun getDownloadSpeed(): Long {
        val networkCapabilities = getNetworkCapabilities() ?: return 0

        return (networkCapabilities.linkDownstreamBandwidthKbps / 1000).toLong()
    }

    // upload speed
    private fun getUploadSpeed(): Long {
        val networkCapabilities = getNetworkCapabilities() ?: return 0

        return (networkCapabilities.linkUpstreamBandwidthKbps / 1000).toLong()
    }

    // line chart
    private fun LineChart.init(percentArray: IntArray) {
        val values = mutableListOf<Entry>()
        percentArray.forEachIndexed { index, value ->
            values.add(Entry(index * 1f, value * 1f))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.lineWidth = 1.75f
        set1.circleRadius = 5f
        set1.circleHoleRadius = 2.5f
        set1.setDrawFilled(true)
        set1.fillColor = ContextCompat.getColor(this@NetworkAnalysisActivity, R.color.colorPrimaryDark)
        set1.color = ContextCompat.getColor(this@NetworkAnalysisActivity, R.color.colorPrimary)
        set1.setCircleColor(ContextCompat.getColor(this@NetworkAnalysisActivity, R.color.colorPrimary))
        set1.setDrawValues(false)

        // create a data object with the data sets
        val data = LineData(set1)

        this.description?.isEnabled = false
        this.setTouchEnabled(false)
        this.isDragEnabled = true
        this.setScaleEnabled(true)
        this.setPinchZoom(false)

        // set custom chart offsets, automatic offset calculation disabled
        this.setViewPortOffsets(10f, 0f, 10f, 0f)

        // add data
        this.data = data

        this.legend?.isEnabled = false
        this.axisLeft?.isEnabled = false
        this.axisLeft?.spaceTop = 40f
        this.axisLeft?.spaceBottom = 40f
        this.axisRight?.isEnabled = false
        this.xAxis?.isEnabled = false

    }
}