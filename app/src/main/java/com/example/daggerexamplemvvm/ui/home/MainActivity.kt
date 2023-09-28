package com.example.daggerexamplemvvm.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.daggerexamplemvvm.R
import com.example.daggerexamplemvvm.di.ApiService
import com.example.daggerexamplemvvm.model.NasaImageApiResponse
import com.example.daggerexamplemvvm.utils.AppUtils
import com.example.daggerexamplemvvm.utils.AppUtils.cacheAllData
import com.example.daggerexamplemvvm.utils.MyProgressDialogFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nasa_image_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var apiService: ApiService

    lateinit var exoPlayer: Player

    lateinit var videoLayout: RelativeLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Paper.init(this)
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set the status bar color to blue
            window.statusBarColor =
                getColor(R.color.blue) // Replace 'R.color.blue' with your desired color resource
        }
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        videoLayout = findViewById<RelativeLayout>(R.id.videoLayout)
        NasaImage?.visibility = View.GONE
        if (AppUtils.isConnectedToMobileData(this) || AppUtils.isConnectedToWifi(this)) {
            cacheAllData(
                applicationContext,
                this,
                mainActivityViewModel,
                ivNasaImage,
                tvTitle,
                tvDate,
                tvDesc,
                NasaImage,
                videoLayout
            )
        }
        clicks()
        getData()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.playWhenReady = false
        exoPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.playWhenReady = false
        exoPlayer.release() // Release the player when the activity is destroyed
    }

    private fun clicks() {
        val sync = Toolbar.ivSync
        sync.setOnClickListener {
            if (!AppUtils.isConnectedToMobileData(this) && !AppUtils.isConnectedToWifi(this)) {
                NasaImage?.visibility = View.VISIBLE
                videoLayout?.visibility = View.GONE
            } else {
                NasaImage?.visibility = View.GONE
                videoLayout?.visibility = View.GONE
                val progressDialog = MyProgressDialogFragment()
                progressDialog.show(supportFragmentManager, "progressDialog")
                mainActivityViewModel.makeApiCall(progressDialog)
            }
        }
    }

    // In this getData() method through observer we are getting the data via live data, the response is either in
    // 200 OK or in 400...500 ranges.
    // Then storing the fetched data in a no-sql based database

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        mainActivityViewModel.getLiveDataListObserver().observe(this) { itemList ->
            if (itemList != null) {
                Log.d("MainActivity", "itemList $itemList")
                Paper.book().write<NasaImageApiResponse>("nasa_api_response", itemList)
                AppUtils.showData(
                    itemList.media_type,
                    ivNasaImage,
                    tvTitle,
                    tvDate,
                    tvDesc,
                    itemList.url,
                    itemList.title,
                    itemList.date,
                    itemList.explanation,
                    applicationContext,
                    NasaImage,
                    videoLayout,
                    this
                )

            } else {
                var data = Paper.book().read<NasaImageApiResponse>(
                    "nasa_api_response",
                    NasaImageApiResponse("", "", "", "", "", "", "", "")
                )
                if (data != null && !data.title.isNullOrEmpty()) {
                    AppUtils.showData(
                        itemList?.media_type,
                        ivNasaImage,
                        tvTitle,
                        tvDate,
                        tvDesc,
                        data.url,
                        data.title,
                        data.date,
                        data.explanation,
                        applicationContext,
                        NasaImage,
                        videoLayout,
                        this
                    )
                } else {
                    Log.d("MainActivity", "error is ${getData()}")
                    AppUtils.showCustomDialog(this, "No Internet", "Please try again")
                    NasaImage?.visibility = View.GONE
                    mainActivityViewModel.getLiveDataListObserver().removeObservers(this)
                }
            }
        }

        // Caching the data is implemented here via a method call.

        cacheAllData(
            applicationContext,
            this,
            mainActivityViewModel,
            ivNasaImage,
            tvTitle,
            tvDate,
            tvDesc,
            NasaImage,
            videoLayout
        )
    }
}