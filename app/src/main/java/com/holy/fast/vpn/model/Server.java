package com.holy.fast.vpn.model;

import android.content.Context;

public abstract class Server {
    private String country;
    private String ovpn;
    private String ovpnUserName;
    private String ovpnUserPassword;


    public Server() {
    }


    public Server(String country, String ovpn, String ovpnUserName, String ovpnUserPassword) {
        this.country = country;
        this.ovpn = ovpn;
        this.ovpnUserName = ovpnUserName;
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public String getCountry() {
        return country;
    }
    public String getOvpn() {
        return ovpn;
    }
    public String getOvpnUserName() {
        return ovpnUserName;
    }
    public String getOvpnUserPassword() {
        return ovpnUserPassword;
    }
    public abstract  String  getConnectionString(Context context);
}
