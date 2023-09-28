package com.example.daggerexamplemvvm.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.daggerexamplemvvm.di.ApiService
import com.example.daggerexamplemvvm.model.NasaImageApiResponse
import com.example.daggerexamplemvvm.utils.MyProgressDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

open class MainActivityViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    @Inject
    lateinit var apiService: ApiService


    private var itemsList: MutableLiveData<NasaImageApiResponse>

    init {
        (application as MyApplication).getRetroComponent().inject(this)
        itemsList = MutableLiveData()
    }

    fun getLiveDataListObserver(): MutableLiveData<NasaImageApiResponse> {
        return itemsList
    }

    fun makeApiCall(progressDialog: MyProgressDialogFragment) {

        var apiKey = "8S3ytGWYw74JNy9f5nVMEQ3sRoMsvgGYYAUWiN1p"

        val call = apiService.getImage(apiKey)

        call.enqueue(
            object : Callback<NasaImageApiResponse> {
                override fun onResponse(
                    call: Call<NasaImageApiResponse>,
                    response: Response<NasaImageApiResponse>
                ) {
                    if (response.isSuccessful) {
                        progressDialog.dismiss()

                        val data = response.body()
                        itemsList.postValue(data)

                    } else {
                        val errorBody = response.errorBody()
                        Log.d("MainActivity", "error1 ${errorBody.toString()}")
                        itemsList.postValue(null)
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<NasaImageApiResponse>, t: Throwable) {
                    Log.d("MainActivity", "error2 ${t.message}")
                    itemsList.postValue(null)
                    progressDialog.dismiss()
                }
            }
        )

    }

    fun makeApiCall() {

        var apiKey = "8S3ytGWYw74JNy9f5nVMEQ3sRoMsvgGYYAUWiN1p"

        val call = apiService.getImage(apiKey)

        call.enqueue(
            object : Callback<NasaImageApiResponse> {
                override fun onResponse(
                    call: Call<NasaImageApiResponse>,
                    response: Response<NasaImageApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        itemsList.postValue(data)

                    } else {
                        val errorBody = response.errorBody()
                        Log.d("MainActivity", "error1 ${errorBody.toString()}")
                        itemsList.postValue(null)
                    }
                }

                override fun onFailure(call: Call<NasaImageApiResponse>, t: Throwable) {
                    Log.d("MainActivity", "error2 ${t.message}")
                    itemsList.postValue(null)
                }
            }
        )

    }
}