package com.example.daggerexamplemvvm.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.daggerexamplemvvm.R
import com.example.daggerexamplemvvm.model.NasaImageApiResponse
import com.example.daggerexamplemvvm.ui.home.MainActivity
import com.example.daggerexamplemvvm.ui.home.MainActivityViewModel
import io.paperdb.Paper
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import java.time.LocalDate
import java.util.*

object AppUtils {

    // To check if the current time is mid-night or not, as
    // Nasa image of the day api gives new images over every (after) mid-night.
    // This check will be performed every time the app is opened.

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkIfMidNight(context: Context): Boolean {
        Paper.init(context)
        val currentTime = Calendar.getInstance()
        val currentDate = LocalDate.now()
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when {
            currentTime.before(midnight) -> {
                false
            }
            currentTime.after(midnight) -> {
                Paper.book().write("today", currentDate.toString())
                true
            }
            else -> {
                Paper.book().write("today", currentDate.toString())
                true
            }
        }
    }

    // This compare two dates is always called when the app is
    // opened by the user.
    // As this compareDates() will determine, if at the point of time when
    // User will open the app.or use the app to get the Nasa image of the day
    // Then the user should be able to get the cached data, in case the mid-night is not hitted.
    // If the mid-night is already passed then there will be an api call to the NASA server, to
    // fetch the new image of the day picture.
    // And the current date will be the new date, after mid-night is hitted.

    @RequiresApi(Build.VERSION_CODES.O)
    fun compareDates(cDate: String): Boolean {
        val currentDate = LocalDate.now()
        val comparisonResult = cDate.compareTo(currentDate.toString())
        return when {
            comparisonResult < 0 -> {
                Paper.book().write("today", currentDate.toString())
                false
            }
            comparisonResult > 0 -> {
                false
            }
            else -> true
        }
    }

    // This showData() will display the data accordingly.

    fun showData(
        media_type: String?,
        ivImage: ImageView?,
        tvTitle: TextView?,
        tvDate: TextView?,
        tvDesc: TextView?,
        url: String?,
        title: String?,
        date: String?,
        desc: String?,
        context: Context?,
        NasaImage: View,
        videoLayout: RelativeLayout,
        mainActivity: MainActivity
    ) {
        if (!media_type.isNullOrEmpty() && media_type!!.contains("image")) {
            Glide.with(context!!).load(url).centerCrop().into(ivImage!!)
            tvTitle?.text = "Title :  $title"
            tvDate?.text = "Date : $date"
            tvDesc?.text = "Description : $desc"
            if (title.isNullOrEmpty()) {
                NasaImage.visibility = View.GONE
                showCustomDialog(
                    mainActivity,
                    "Server Error",
                    "Server is temporarily down, please try after sometime"
                )
            } else {
                NasaImage.visibility = View.VISIBLE
                videoLayout.visibility = View.GONE
            }
        } else {
            NasaImage.visibility = View.GONE
            videoLayout.visibility = View.VISIBLE
        }
    }

    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } else {
            // For devices running Android versions prior to M (API level 23),
            // use a different approach to check Wi-Fi connection.
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }

    fun isConnectedToMobileData(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        } else {
            // For devices running Android versions prior to M (API level 23),
            // use a different approach to check mobile data connection.
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_MOBILE
        }
    }

    // A custom dialog, to show the users about the current status of the application.

    fun showCustomDialog(activity: Activity, title: String, message: String) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_alert_dialog, null)
        val button = dialogView.findViewById<Button>(R.id.dialog_button)
        dialogView?.tvDialogTitle?.text = title
        dialogView?.tvDialogMessage?.text = message
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        button.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    fun isDeviceConnectedToInternet(context: Context): Boolean {

        /**
         *  This method checks if the device is connected to an active internet connection.
         *  Return boolean value for connected or not-connected
         */

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
            return activeNetwork != null &&
                    (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cacheAllData(
        context: Context,
        activity: Activity,
        mainActivityViewModel: MainActivityViewModel,
        ivNasaImage: ImageView,
        tvTitle: TextView,
        tvDate: TextView,
        tvDesc: TextView,
        NasaImage: View,
        videoLayout: RelativeLayout
    ) {
        var resp = Paper.book().read<NasaImageApiResponse>(
            "nasa_api_response",
            NasaImageApiResponse("", "", "", "", "", "", "", "")
        )
        var cDate = Paper.book().read("today", "")

        if (resp?.title.isNullOrEmpty() && (isConnectedToWifi(context) || isConnectedToMobileData(
                context
            ))
        ) {
            mainActivityViewModel.makeApiCall()
        } else {
            val progressDialog = MyProgressDialogFragment()
            if (checkIfMidNight(context)) {
                if (cDate.isNullOrEmpty()) {
                    mainActivityViewModel.makeApiCall(progressDialog)
                } else if (!cDate.isNullOrEmpty()) {
                    if (!compareDates(cDate)) {
                        mainActivityViewModel.makeApiCall(progressDialog)
                    } else {
                        if (resp != null) {
                            if (resp.title != null) {
                                if (resp.hdurl != null &&
                                    resp.date != null &&
                                    resp.explanation != null &&
                                    resp.media_type != null &&
                                    resp.url != null &&
                                    resp.title != null
                                ) {
                                    showData(
                                        resp.media_type,
                                        ivNasaImage,
                                        tvTitle,
                                        tvDate,
                                        tvDesc,
                                        resp.url,
                                        resp.title,
                                        resp.date,
                                        resp.explanation,
                                        context,
                                        NasaImage,
                                        videoLayout,
                                        activity as MainActivity
                                    )
                                } else NasaImage?.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    if (resp != null) {
                        if (resp.hdurl != null &&
                            resp.date != null &&
                            resp.explanation != null &&
                            resp.media_type != null &&
                            resp.url != null &&
                            resp.title != null
                        ) {
                            showData(
                                resp.media_type,
                                ivNasaImage,
                                tvTitle,
                                tvDate,
                                tvDesc,
                                resp.hdurl,
                                resp.title,
                                resp.date,
                                resp.explanation,
                                context,
                                NasaImage,
                                videoLayout,
                                activity as MainActivity
                            )
                        } else NasaImage?.visibility = View.GONE
                    }
                }
            }
        }
    }
}
