package com.jzci.portal

import android.app.Application
import com.jzci.portal.data.api.ApiService

class JZCIPortalApp : Application() {

    lateinit var apiService: ApiService
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Default API URL - can be configured for production
        apiService = ApiService("https://jzci-portal.onrender.com")
    }

    companion object {
        lateinit var instance: JZCIPortalApp
            private set
    }
}
