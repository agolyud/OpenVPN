package com.holy.fast.vpn.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.holy.fast.vpn.CheckInternetConnection
import com.holy.fast.vpn.R
import com.holy.fast.vpn.SharedPreference
import com.holy.fast.vpn.model.Server
import com.holy.fast.vpn.model.StaticServer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.IOException

class MainFragment : Fragment() {
    private lateinit var server: Server
    private lateinit var connection: CheckInternetConnection
    private var vpnStart = false
    private lateinit var preference: SharedPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        initializeAll()
        return view
    }

    private fun initializeAll() {
        preference = SharedPreference(requireContext())
        server = preference.getServer()
        connection = CheckInternetConnection()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isServiceRunning
        VpnStatus.initLogCache(requireActivity().cacheDir)
    }

    override fun onPause() {
        super.onPause()
        stopVpn()
    }

    override fun onResume() {
        super.onResume()
        prepareVpn()
    }

    private fun prepareVpn() {
        when {
            !vpnStart && internetStatus -> {
                val intent = VpnService.prepare(context)
                if (intent != null) startActivityForResult(intent, 1) else startVpn()
            }
            !vpnStart -> showToast("you have no internet connection !!")
            stopVpn() -> showToast("Disconnect Successfully")
        }
    }

    fun stopVpn(): Boolean {
        return try {
            OpenVPNThread.stop()
            vpnStart = false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) startVpn() else showToast("Permission Deny !! ")
    }

    val internetStatus: Boolean
        get() = connection.netCheck(context)

    val isServiceRunning: Unit
        get() = setStatus(OpenVPNService.getStatus())

    private fun startVpn() {
        try {
            OpenVpnApi.startVpn(context, server.getConnectionString(context), server.country, server.ovpnUserName, server.ovpnUserPassword)
            logTv.text = "Connecting..."
            vpnStart = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun setStatus(connectionState: String?) {
        when (connectionState) {
            "DISCONNECTED" -> {
                vpnStart = false
                OpenVPNService.setDefaultStatus()
                logTv.text = ""
            }
            "CONNECTED" -> {
                vpnStart = true
                logTv.text = ""
            }
            "WAIT" -> logTv.text = "waiting for server connection!!"
            "AUTH" -> logTv.text = "server authenticating!!"
            "RECONNECTING" -> logTv.text = "Reconnecting..."
            "NONETWORK" -> logTv.text = "No network connection"
        }
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                setStatus(intent.getStringExtra("state"))
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}