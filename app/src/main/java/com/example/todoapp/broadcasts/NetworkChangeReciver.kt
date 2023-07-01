//package com.example.todoapp.broadcasts
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import androidx.lifecycle.MutableLiveData
//import com.example.todoapp.ui.viewmodels.HomeViewModel
//
//class NetworkChangeReceiver : BroadcastReceiver() {
//
//    private val isConnectedLiveData = MutableLiveData<Boolean>()
//
//    val isConnected: MutableLiveData<Boolean>
//        get() = isConnectedLiveData
//
//    override fun onReceive(context: Context, intent: Intent) {
//
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        val network = connectivityManager.activeNetwork
//
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
//
//        val isConnected = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            ?: false
//
//        isConnectedLiveData.postValue(isConnected)
//    }
//
//}