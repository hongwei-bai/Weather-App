package au.com.test.weather_app.recent

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import au.com.test.weather_app.R
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.share.adapter.RecentRecordListAdapter
import au.com.test.weather_app.share.adapter.WorkMode.Delete
import kotlinx.android.synthetic.main.activity_recent_location.*
import javax.inject.Inject

class LocationRecordActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: LocationRecordViewModel

    private lateinit var recentRecordListAdapter: RecentRecordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_location)

        viewModel = getViewModelProvider(this).get(LocationRecordViewModel::class.java)
        observeViewModelState()
    }

    override fun onResume() {
        super.onResume()
        initializeRecyclerView()
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
            recentRecordListAdapter.apply {
                data = recentRecords
                notifyDataSetChanged()
            }
        })
    }

    private fun initializeRecyclerView() {
        recentRecordListAdapter = RecentRecordListAdapter(this)
        recentRecordListAdapter.workMode = Delete
        recyclerRecent.layoutManager = LinearLayoutManager(this)
        recyclerRecent.adapter = recentRecordListAdapter
        recyclerRecent.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}