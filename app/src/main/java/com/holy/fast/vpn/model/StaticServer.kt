package com.holy.fast.vpn.model

import android.content.Context
import android.util.Base64

class StaticServer : Server {

    constructor() : super() {
        connectionString = ""
    }

    constructor(context: Context, server: Server) : super(server.country, server.ovpn, server.ovpnUserName, server.ovpnUserPassword) {
        connectionString =
                Base64.encodeToString(server.getConnectionString(context).toByteArray(), Base64.DEFAULT)
    }

    val connectionString: String

    override fun getConnectionString(context: Context?): String = connectionString
}