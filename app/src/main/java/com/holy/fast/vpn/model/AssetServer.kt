package com.holy.fast.vpn.model

import android.content.Context

class AssetServer : Server {
    constructor()
    constructor(country: String?, ovpn: String?, ovpnUserName: String?, ovpnUserPassword: String?) : super(country, ovpn, ovpnUserName, ovpnUserPassword) {}

    override fun getConnectionString(context: Context): String {
        val conf = context.assets.open(ovpn)
        return conf.bufferedReader().lineSequence().joinToString(separator = "\n")
    }
}