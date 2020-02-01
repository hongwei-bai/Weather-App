package au.com.test.weather_app.home.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.test.weather_app.R
import kotlinx.android.synthetic.main.layout_searchbar.view.*


class LocationSearchToolBar : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        initialize(context)
    }

    var title: String = ""
        set(value) {
            field = value
            updateTitle()
            switchSearchMode(false)
        }

    private var onGpsButtonClickListener: (() -> Unit)? = null

    private var onSearchButtonClickListener: ((String) -> Unit)? = null

    fun setOnGpsButtonClick(listener: (() -> Unit)) {
        this.onGpsButtonClickListener = listener
    }

    fun setOnSearchButtonClick(listener: ((String) -> Unit)) {
        this.onSearchButtonClickListener = listener
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_searchbar, this)

        updateTitle()
        registerGpsButtonClickListener()
        registerSearchButtonClickListener()
        registerTitleClickListener()
    }

    private fun switchSearchMode(searchOn: Boolean) {
        if (searchOn) {
            imgBtnGps.visibility = View.VISIBLE
            imgBtnSearch.visibility = View.VISIBLE
            editTxtSearh.visibility = View.VISIBLE
            txtTitle.visibility = View.INVISIBLE
        } else {
            imgBtnGps.visibility = View.INVISIBLE
            imgBtnSearch.visibility = View.INVISIBLE
            editTxtSearh.visibility = View.INVISIBLE
            txtTitle.visibility = View.VISIBLE
        }
    }

    private fun registerGpsButtonClickListener() {
        imgBtnGps.setOnClickListener {
            onGpsButtonClickListener?.invoke()
        }
    }

    private fun registerSearchButtonClickListener() {
        imgBtnSearch.setOnClickListener {
            onSearchButtonClickListener?.invoke(editTxtSearh.text.toString())
        }
        listenToExternalKeyboardEnterKey()
    }

    private fun listenToExternalKeyboardEnterKey() =
        editTxtSearh.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) { //do what you want on the press of 'done'
                onSearchButtonClickListener?.invoke(editTxtSearh.text.toString())
            }
            false
        }

    private fun registerTitleClickListener() {
        txtTitle.setOnClickListener {
            editTxtSearh.setText("")
            switchSearchMode(true)
        }
    }

    private fun updateTitle() {
        txtTitle.text = title
    }
}