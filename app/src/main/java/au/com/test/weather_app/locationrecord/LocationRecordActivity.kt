package au.com.test.weather_app.locationrecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import au.com.test.weather_app.R
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.LeftButton
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.RightButton
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.SecondaryRightButton
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.Delete
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.MultipleDelete
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import kotlinx.android.synthetic.main.activity_location_record.*
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

    private lateinit var locationRecordListAdapter: LocationRecordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_record)

        viewModel = getViewModelProvider(this).get(LocationRecordViewModel::class.java)
        observeViewModelState()
        initializeRecyclerView()
        initializeToolbar()
        initializeFloatingActionButton()
    }

    override fun onResume() {
        super.onResume()

        viewModel.go()
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
                data = recentRecords
                notifyDataSetChanged()
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
            setOnItemClickListener { _, _ ->
                if (workMode == MultipleDelete) {
                    layoutToolbar?.getButton(SecondaryRightButton)?.isSelected = isAllSelected()
                }
                updateFloatingActionButtonVisibility()
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
                R.id.actionDeleteSingle -> locationRecordListAdapter.workMode = Delete
                R.id.actionDeleteMultiple -> locationRecordListAdapter.workMode = MultipleDelete
            }
            layoutToolbar.enableButton(
                SecondaryRightButton,
                it.itemId == R.id.actionDeleteMultiple && locationRecordListAdapter.itemCount > 0
            )
            true
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
}