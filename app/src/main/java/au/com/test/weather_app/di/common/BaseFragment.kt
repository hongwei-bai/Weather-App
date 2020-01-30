package au.com.test.weather_app.di.common

import android.content.Context
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment

open class BaseFragment : DaggerFragment() {
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}