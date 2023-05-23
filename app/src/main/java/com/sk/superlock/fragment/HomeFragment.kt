package com.sk.superlock.fragment

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sk.superlock.R
import com.sk.superlock.activity.NetworkAnalysisActivity
import com.sk.superlock.databinding.FragmentHomeBinding
import com.sk.superlock.services.Booster
import com.sk.superlock.util.Constants
import com.sk.superlock.util.JunkCleaner
import com.sk.superlock.util.PrefManager
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPref: PrefManager

    companion object {
        const val TAG: String = "HomeFragment"
        var appsToLock: Int = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPref = PrefManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // junk cleaner
        binding.junkCleaner.setOnClickListener {
            val junkCleaner = JunkCleaner(requireActivity())
            junkCleaner.execute()
        }
        // network analysis
        binding.networkAnalysis.setOnClickListener {
            startActivity(Intent(requireContext(),NetworkAnalysisActivity::class.java))
        }
        // booster
        binding.booster.setOnClickListener {
            val booster = Booster(requireActivity())
            booster.execute()
        }

        // app lock
        binding.cvAppLock.setOnClickListener {
            navigateToFragment(ApplicationsFragment())
        }
        appsToLock = sharedPref.getInt(Constants.AVAILABLE_APPS_SIZE)
        binding.tvAvailableAppsToLock.text = buildString {
            append(appsToLock)
            append(" ")
            append(resources.getString(R.string.no_apps_needs_protection))
        }
        binding.btnProtect.setOnClickListener {
            navigateToFragment(ApplicationsFragment())
        }

        // memory space
        binding.btnBoost.setOnClickListener {
            Toast.makeText(requireContext(), "Memory Space boost", Toast.LENGTH_SHORT).show()
        }
        val (totalMemory, usedMemory) = getMemoryInfo()
        binding.tvAvailableMemory.text = buildString {
            append(usedMemory)
            append(" GB / ")
            append(totalMemory)
            append(" GB")
        }
        val memoryPercentage = getMemoryInfoPercentage()
        binding.progressMemory.progress = memoryPercentage

        // storage space
        binding.btnClearStorage.setOnClickListener {
            Toast.makeText(requireContext(), "Clear Storage Space", Toast.LENGTH_SHORT).show()
        }
        val (totalSpace, usedSpace) = getStorageInfo()
        binding.tvAvailableStorage.text = buildString {
            append(usedSpace)
            append(" GB / ")
            append(totalSpace)
            append(" GB")
        }
        val storagePercentage = getStorageInfoPercentage()
        binding.progressStorage.progress = storagePercentage

    }

    // clear cache
//    fun clearCacheForAllApps(context: Context) {
//        val packageManager = context.packageManager
//        val appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//
//        for (app in appList) {
//            if (app.packageName != context.packageName) {
//                val appContext = context.createPackageContext(app.packageName, 0)
//                appContext.deleteApplicationCacheFiles()
//            }
//        }
//    }


    // memory info percentage
    private fun getMemoryInfoPercentage(): Int {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = requireContext().getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem.toDouble()
        val availableMemory = memoryInfo.availMem.toDouble()
        val usedMemory = totalMemory - availableMemory
        return (usedMemory / totalMemory * 100).toInt()
    }


    // get memory info
    private fun getMemoryInfo(): Pair<Double, Double>{
        val activityManager = requireContext().getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val availableMemory = memoryInfo.availMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val decimalFormat = DecimalFormat("#.##")
        val totalGb = decimalFormat.format(totalMemory).toDouble()
        val usedGb = decimalFormat.format(totalMemory - availableMemory).toDouble()

        return Pair(totalGb, usedGb)
    }

    // get storage space
    private fun getStorageInfo(): Pair<Double, Double> {
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        val totalSize = totalBlocks * blockSize.toDouble()
        val availableSize = availableBlocks * blockSize.toDouble()
        val usedSize = totalSize - availableSize
        val gb = 1024.0 * 1024.0 * 1024.0
        val decimalFormat = DecimalFormat("#.##")
        val totalGb = decimalFormat.format(totalSize / gb).toDouble()
        val usedGb = decimalFormat.format(usedSize / gb).toDouble()

        return Pair(totalGb, usedGb)
    }

    // get storage space in percentage
    private fun getStorageInfoPercentage(): Int {
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        val totalSize = totalBlocks * blockSize.toDouble()
        val availableSize = availableBlocks * blockSize.toDouble()
        val usedSize = totalSize - availableSize

        return ((usedSize / totalSize) * 100).toInt()
    }

    // change fragment
    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}