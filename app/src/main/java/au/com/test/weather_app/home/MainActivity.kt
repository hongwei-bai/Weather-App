package au.com.test.weather_app.home

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.home.search.SearchSuggestionListAdapter
import au.com.test.weather_app.locationrecord.LocationRecordActivity
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.LeftButton
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.LeftButtonOnSearchMode
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.RightButtonOnSearchMode
import au.com.test.weather_app.uicomponents.adapter.LocationRecordListAdapter
import au.com.test.weather_app.uicomponents.model.Default
import au.com.test.weather_app.uicomponents.model.Error
import au.com.test.weather_app.uicomponents.model.Loading
import au.com.test.weather_app.uicomponents.model.Success
import au.com.test.weather_app.util.gone
import au.com.test.weather_app.util.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val REQUEST_CODE_LOCATION = 444
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var locationRecordListAdapter: LocationRecordListAdapter

    private lateinit var searchSuggestionListAdapter: SearchSuggestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = getViewModelProvider(this).get(MainViewModel::class.java)
        observeViewModelState()

        initializeRecyclerView()
        initializeSearchSuggestionRecyclerView()
        initializeToolbar()
        initializeSwipeRefreshLayout()

        viewModel.initializeCityIndexTable()
    }

    override fun onResume() {
        super.onResume()

        viewModel.go()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        getWeatherForCurrentLocation()
    }

    override fun onBackPressed() {
        if (layoutToolbar.isOnSearchMode()) {
            layoutToolbar.onLostFocus()
            resetSearchSuggestion()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRefresh() {
        layoutSwipeRefresh.isRefreshing = true
        viewModel.go()
        locationRecordListAdapter.notifyDataSetChanged()
    }

    override fun inject() {
        DaggerActivityComponent.builder()
            .applicationComponent(getAppComponent())
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }

    private fun observeViewModelState() {
        viewModel.currentWeatherState.observe(this, Observer {
            when (it) {
                is Loading -> onCurrentWeatherDataStartLoading()
                is Success<*> -> onCurrentWeatherDataLoadSuccess(it.data as WeatherData)
                is Default -> onCurrentWeatherDataEmpty()
                is Error -> onCurrentWeatherDataLoadError()
            }
        })

        viewModel.recentRecords.observe(this, Observer { recentRecords ->
            locationRecordListAdapter.apply {
                data = recentRecords
                notifyDataSetChanged()
            }
        })

        viewModel.searchSuggestions.observe(this, Observer { list ->
            recyclerSearchSuggestion.show(list?.size ?: 0 > 0)
            searchSuggestionListAdapter.data = list
        })
    }

    private fun onCurrentWeatherDataStartLoading() {
        resetSearchSuggestion()
        layoutSwipeRefresh.isRefreshing = true
    }

    private fun onCurrentWeatherDataLoadSuccess(weatherData: WeatherData) {
        layoutCurrentLocationWeather.update(weatherData)
        layoutCurrentLocationWeather.setLoading(false) {
            layoutSwipeRefresh.isRefreshing = false
        }
        divider.show()
        layoutToolbar.title = weatherData.getCityTitle() ?: getString(
            R.string.unknown_location,
            weatherData.latitude,
            weatherData.longitude
        )
        txtTitle.clearFocus()
        hideKeyboard()
    }

    private fun onCurrentWeatherDataLoadError() {
        layoutSwipeRefresh.isRefreshing = false
        layoutCurrentLocationWeather.showError()
        Snackbar.make(layoutSwipeRefresh, R.string.general_error, Snackbar.LENGTH_LONG).show()
    }

    private fun onCurrentWeatherDataEmpty() {
        layoutSwipeRefresh.isRefreshing = false
        layoutToolbar.switchSearchMode(true)
        layoutCurrentLocationWeather.showWelcome()
    }

    private fun initializeSwipeRefreshLayout() {
        layoutSwipeRefresh.setOnRefreshListener(this)
    }

    private fun initializeRecyclerView() {
        locationRecordListAdapter = LocationRecordListAdapter(this)
        recyclerLocationRecord.layoutManager = LinearLayoutManager(this)
        recyclerLocationRecord.adapter = locationRecordListAdapter
        recyclerLocationRecord.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        locationRecordListAdapter.setOnItemClickListener { _, weatherData ->
            viewModel.fetch(weatherData)
        }
    }

    private fun initializeSearchSuggestionRecyclerView() {
        searchSuggestionListAdapter = SearchSuggestionListAdapter(this)
        recyclerSearchSuggestion.layoutManager = LinearLayoutManager(this)
        recyclerSearchSuggestion.adapter = searchSuggestionListAdapter
        recyclerSearchSuggestion.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        recyclerSearchSuggestion.show()
        searchSuggestionListAdapter.setOnItemClickListener { _, cityData ->
            resetSearchSuggestion()
            viewModel.fetch(cityData.name, cityData.countryCode)
        }
    }

    private fun resetSearchSuggestion() {
        layoutToolbar.clearText()
        recyclerSearchSuggestion.gone()
    }

    private fun initializeToolbar() {
        with(layoutToolbar) {
            isEnableSearch = true
            hint = getString(R.string.search_hint)
            leftIcon = R.drawable.selector_edit
            leftIconOnSearchMode = R.drawable.selector_gps
            rightIconOnSearchMode = R.drawable.selector_arrow_forward
            setOnButtonClick { button, input, _ ->
                when (button) {
                    LeftButton -> startActivity(LocationRecordActivity.intent(context))
                    LeftButtonOnSearchMode -> {
                        resetSearchSuggestion()
                        getWeatherForCurrentLocation()
                    }
                    RightButtonOnSearchMode -> {
                        resetSearchSuggestion()
                        viewModel.fetch(input)
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
            setOnTextWatchListener { text, _ -> viewModel.onSearchTextChange(text) }
        }
    }

    private fun hasLocationPermission(): Boolean =
        EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private fun getWeatherForCurrentLocation() {
        if (hasLocationPermission()) {
            try {
                (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    .subscribeCurrentLocation { viewModel.fetch(it.latitude, it.longitude) }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.permission_request_location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}