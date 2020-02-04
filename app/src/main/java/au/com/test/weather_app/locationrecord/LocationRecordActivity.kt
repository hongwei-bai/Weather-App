package au.com.test.weather_app.locationrecord

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import au.com.test.weather_app.R
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.LeftButton
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.RightButton
import au.com.test.weather_app.uicomponents.WeatherToolBar.ToolbarButton.SecondaryRightButton
import au.com.test.weather_app.uicomponents.adapter.LocationRecordListAdapter
import au.com.test.weather_app.uicomponents.adapter.LocationRecordListAdapter.WorkMode
import au.com.test.weather_app.uicomponents.adapter.LocationRecordListAdapter.WorkMode.Delete
import au.com.test.weather_app.uicomponents.adapter.LocationRecordListAdapter.WorkMode.MultipleDelete
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.home.MainViewModel
import au.com.test.weather_app.uicomponents.model.Loading
import au.com.test.weather_app.uicomponents.model.Error
import au.com.test.weather_app.uicomponents.model.Success
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.activity_location_record.*
import kotlinx.android.synthetic.main.dialog_weather.*
import javax.inject.Inject


class LocationRecordActivity : BaseActivity() {
    companion object {
        fun intent(context: Context): Intent =
            Intent(context, LocationRecordActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    @Inject
    lateinit var viewModel: LocationRecordViewModel

    @Inject
    lateinit var mainViewModel: MainViewModel

    private lateinit var locationRecordListAdapter: LocationRecordListAdapter

    private var weatherDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_record)

        viewModel = getViewModelProvider(this).get(LocationRecordViewModel::class.java)
        mainViewModel = getViewModelProvider(this).get(MainViewModel::class.java)
        observeViewModelState()
        initializeRecyclerView()
        initializeToolbar()
        initializeFloatingActionButton()
    }

    override fun onResume() {
        super.onResume()

        viewModel.go()
    }

    override fun onPause() {
        weatherDialog?.dismiss()
        weatherDialog = null
        super.onPause()
    }

    override fun inject() {
        DaggerActivityComponent.builder()
            .applicationComponent(getAppComponent())
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }

    private fun observeViewModelState() {
        viewModel.recentRecords.observe(this, Observer { recentRecords ->
            locationRecordListAdapter.apply {
                txtEmpty.show(recentRecords.isEmpty())
                data = recentRecords
                notifyDataSetChanged()
            }
        })
        mainViewModel.currentWeatherState.observe(this, Observer {
            when (it) {
                is Error -> weatherDialog?.layoutWeather?.showError()
                is Success<*> -> weatherDialog?.layoutWeather?.apply {
                    update(it.data as WeatherData)
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        locationRecordListAdapter = LocationRecordListAdapter(this)
        locationRecordListAdapter.workMode = Delete
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = locationRecordListAdapter
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        with(locationRecordListAdapter) {
            setOnItemClickListener { _, weather ->
                when (workMode) {
                    Delete -> promptDialog(weather)
                    MultipleDelete -> {
                        layoutToolbar?.getButton(SecondaryRightButton)?.isSelected = isAllSelected()
                        updateFloatingActionButtonVisibility()
                    }
                    else -> {
                        // DO nothing
                    }
                }
            }
            setOnItemDeleteClickListener { _, weatherData -> viewModel.delete(weatherData) }
        }
    }

    private fun initializeToolbar() {
        with(layoutToolbar) {
            isEnableSearch = false
            leftIcon = R.drawable.selector_back
            rightIcon = R.drawable.selector_menu
            secondaryRightIcon = R.drawable.selector_selectall
            setOnButtonClick { button, _, view ->
                when (button) {
                    LeftButton -> finish()
                    RightButton -> popupMenu(view)
                    SecondaryRightButton -> toggleSelectAll(view.isSelected)
                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    private fun toggleSelectAll(isChecked: Boolean) {
        if (isChecked) {
            locationRecordListAdapter.selectAll()
        } else {
            locationRecordListAdapter.selectNone()
        }
        updateFloatingActionButtonVisibility()
    }

    private fun popupMenu(anchor: View) = PopupMenu(this, anchor).apply {
        menuInflater.inflate(R.menu.menu_location_record, menu)
        show()
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.actionDeleteSingle -> switchWorkMode(Delete)
                R.id.actionDeleteMultiple -> switchWorkMode(MultipleDelete)
            }
            true
        }
    }

    private fun switchWorkMode(workMode: WorkMode) {
        when (workMode) {
            Delete -> {
                locationRecordListAdapter.workMode = Delete
                layoutToolbar.enableButton(SecondaryRightButton, false)
            }
            MultipleDelete -> {
                locationRecordListAdapter.workMode = MultipleDelete
                layoutToolbar.enableButton(
                    SecondaryRightButton,
                    locationRecordListAdapter.itemCount > 0
                )
            }
            else -> {
                // Do nothing
            }
        }
    }

    private fun initializeFloatingActionButton() {
        fab.setOnClickListener {
            clearButtonStateWhenDeleteAll()
            viewModel.delete(locationRecordListAdapter.getSelectedItems())
        }
    }

    private fun clearButtonStateWhenDeleteAll() {
        if (locationRecordListAdapter.isAllSelected()) {
            layoutToolbar.getButton(SecondaryRightButton).isSelected = false
            layoutToolbar.enableButton(SecondaryRightButton, false)
        }
        fab.hide()
    }

    private fun updateFloatingActionButtonVisibility() {
        with(locationRecordListAdapter) {
            fab?.visibility = if (workMode == MultipleDelete && getSelectedCount() > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun promptDialog(data: WeatherData) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this).apply {
            if (data.getCityTitle() != null) {
                setTitle(data.getCityTitle())
            } else {
                setTitle(getString(R.string.unknown_location_prefix))
                setMessage(getString(R.string.gps_location, data.latitude, data.longitude))
            }
            setView(View.inflate(context, R.layout.dialog_weather, null))
            setCancelable(true)
        }
        weatherDialog = builder.create().apply {
            show()
            layoutWeather.setLoading()
            setOnDismissListener { weatherDialog = null }
        }
        mainViewModel.fetch(data)
    }
}