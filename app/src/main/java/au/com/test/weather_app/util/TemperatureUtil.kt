package au.com.test.weather_app.util

object TemperatureUtil {
    fun kalvinToCelsius(kelvin: Float) = kelvin - 273.15f

    fun fahrenheitToCelsius(fahrenheit: Float): Float = (fahrenheit - 32) * 5 / 9

    fun celsiusToFahrenheit(celsius: Float): Float = celsius * (9 / 5) + 32

    fun kalvinToFahrenheit(kelvin: Float) = celsiusToFahrenheit(kelvin - 273.15f)
}