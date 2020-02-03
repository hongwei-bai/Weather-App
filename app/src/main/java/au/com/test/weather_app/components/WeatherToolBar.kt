package au.com.test.weather_app.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import au.com.test.weather_app.R
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.LeftButton
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.LeftButtonOnSearchMode
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.RightButton
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.RightButtonOnSearchMode
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.SecondaryRightButton
import au.com.test.weather_app.util.hide
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class WeatherToolBar : ConstraintLayout {

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

    var isEnableSearch: Boolean = true
        set(value) {
            field = value
            switchSearchMode(field && isOnSearchMode())
        }

    fun isOnSearchMode(): Boolean = editTxtSearh?.visibility == View.VISIBLE

    @DrawableRes
    var leftIcon: Int? = null
        set(value) {
            field = value
            switchSearchMode()
        }

    @DrawableRes
    var rightIcon: Int? = null
        set(value) {
            field = value
            switchSearchMode()
        }

    @DrawableRes
    var secondaryRightIcon: Int? = null

    @DrawableRes
    var leftIconOnSearchMode: Int? = null
        set(value) {
            field = value
            switchSearchMode()
        }

    @DrawableRes
    var rightIconOnSearchMode: Int? = null
        set(value) {
            field = value
            switchSearchMode()
        }

    var hint: String? = null
        set(value) {
            field = value
            editTxtSearh.hint = value
        }

    private var onButtonClickListener: ((event: ToolbarButton, text: String, view: View) -> Unit)? = null

    private var onTextChangeListener: ((text: String, view: View) -> Unit)? = null

    fun setOnButtonClick(listener: ((ToolbarButton, String, View) -> Unit)) {
        this.onButtonClickListener = listener
    }

    fun setOnTextWatchListener(listener: (String, View) -> Unit) {
        onTextChangeListener = listener
    }

    fun onLostFocus() {
        switchSearchMode(false)
    }

    fun clearText() {
        editTxtSearh.setText("")
    }

    fun enableButton(buttonId: ToolbarButton, visible: Boolean) {
        buttonId.visible = visible
        switchSearchMode()
    }

    fun getButton(buttonId: ToolbarButton): ImageButton = when (buttonId) {
        LeftButton, LeftButtonOnSearchMode -> imgBtnLeft
        RightButton, RightButtonOnSearchMode -> imgBtnRight
        SecondaryRightButton -> imgBtnRight2
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_toolbar, this)
        switchSearchMode(isEnableSearch)

        updateTitle()
        registerButtonClickListener()
        listenToExternalKeyboardEnterKey()
        registerTitleClickListener()
        registerTextChangeListener()
    }

    fun switchSearchMode(searchOn: Boolean = isOnSearchMode()) {
        fun showIfIconDefined(imageButton: ImageButton, buttonId: ToolbarButton, @DrawableRes drawable: Int?) {
            with(imageButton) {
                if (buttonId.visible && drawable != null) {
                    show()
                    background = ContextCompat.getDrawable(context, drawable)
                } else {
                    hide()
                }
            }
        }

        if (searchOn) {
            editTxtSearh.show()
            txtTitle.hide()
            showIfIconDefined(imgBtnLeft, LeftButtonOnSearchMode, leftIconOnSearchMode)
            showIfIconDefined(imgBtnRight, RightButtonOnSearchMode, rightIconOnSearchMode)
            imgBtnRight2.hide()
        } else {
            editTxtSearh.hide()
            txtTitle.show()
            showIfIconDefined(imgBtnLeft, LeftButton, leftIcon)
            showIfIconDefined(imgBtnRight, RightButton, rightIcon)
            showIfIconDefined(imgBtnRight2, SecondaryRightButton, secondaryRightIcon)
        }
    }

    private fun registerButtonClickListener() {
        imgBtnLeft.setOnClickListener {
            onButtonClickListener?.invoke(
                if (isOnSearchMode()) LeftButtonOnSearchMode else LeftButton,
                editTxtSearh.text.toString(),
                it
            )
        }
        imgBtnRight.setOnClickListener {
            onButtonClickListener?.invoke(
                if (isOnSearchMode()) RightButtonOnSearchMode else RightButton,
                editTxtSearh.text.toString(),
                it
            )
        }
        if (!isOnSearchMode()) {
            imgBtnRight2.setOnClickListener {
                imgBtnRight2.isSelected = !imgBtnRight2.isSelected
                onButtonClickListener?.invoke(SecondaryRightButton, editTxtSearh.text.toString(), it)
            }
        }
    }

    private fun listenToExternalKeyboardEnterKey() =
        editTxtSearh.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) { //do what you want on the press of 'done'
                onButtonClickListener?.invoke(
                    RightButtonOnSearchMode,
                    editTxtSearh.text.toString(),
                    imgBtnRight
                )
            }
            false
        }

    private fun registerTitleClickListener() {
        txtTitle.setOnClickListener {
            if (isEnableSearch) {
                switchSearchMode(true)
            }
            with(editTxtSearh) {
                setText("")
                requestFocus()
            }
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editTxtSearh, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun registerTextChangeListener() {
        editTxtSearh.addTextChangedListener {
            onTextChangeListener?.invoke(editTxtSearh.text.toString(), editTxtSearh)
        }
    }

    private fun updateTitle() {
        txtTitle.text = title
    }

    enum class ToolbarButton(var visible: Boolean) {
        LeftButton(true),
        RightButton(true),
        SecondaryRightButton(true),
        LeftButtonOnSearchMode(true),
        RightButtonOnSearchMode(true);
    }
}