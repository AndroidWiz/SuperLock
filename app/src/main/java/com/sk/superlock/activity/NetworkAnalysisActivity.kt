package com.sk.superlock.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sk.superlock.R
import com.sk.superlock.databinding.ActivityNetworkAnalysisBinding

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
            val downloadSpeed = getDownloadSpeed()
            val uploadSpeed = getUploadSpeed()

            binding.tvDownload.text = "$downloadSpeed MB/s"
            binding.tvUpload.text = "$uploadSpeed MB/s"
        }
    }


    private fun getNetworkCapabilities(): NetworkCapabilities? {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    }

    // download speed
    private fun getDownloadSpeed(): Long {
        val networkCapabilities = getNetworkCapabilities() ?: return 0
        val downloadSpeed = (networkCapabilities.linkDownstreamBandwidthKbps / 1000).toLong()

        return downloadSpeed
    }

    // upload speed
    private fun getUploadSpeed(): Long {
        val networkCapabilities = getNetworkCapabilities() ?: return 0
        val uploadSpeed = (networkCapabilities.linkUpstreamBandwidthKbps / 1000).toLong()

        return uploadSpeed
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