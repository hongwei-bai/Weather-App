package au.com.test.weather_app.di.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import au.com.test.weather_app.WeatherApp
import au.com.test.weather_app.di.components.ActivityComponent
import au.com.test.weather_app.di.utils.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base activity for all activities
 *
 * Warning:
 * If activity has user inactivity timeout it must inherit from [SecureBaseActivity]
 */
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var activityComponent: ActivityComponent

    val subscriptions = CompositeDisposable()

    protected fun getAppComponent() = (applicationContext as WeatherApp).applicationComponent

    abstract fun inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    fun getViewModelProvider(activity: FragmentActivity): ViewModelProvider {
        return ViewModelProviders.of(activity, viewModelFactory)
    }
}