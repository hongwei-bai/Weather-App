package au.com.test.weather_app.test.factory

enum class TestWeatherCondition(val main: String, val desc: String) {
    Cleared("Cleared", "cleared"),
    Snow("Snow", "heavy snow"),
    Rain("Rain", "light rain");
}

enum class TestCity(val cityId: Long?, val cityName: String?, val countryCode: String?, val lon: Double, val lat: Double, val zipCode: Long? = null) {
    Sydney(2147714L, "Sydney", "AU", 151.21, -33.87),
    Melbourne(2158177L, "Melbourne", "AU", 144.96, -37.81),
    SurryHills(null, "Surry Hills", "AU", 151.21, -33.88, 2010),
    Los_Angeles(5368361L, "Los Angeles", "US", -118.24, 34.05),
    SomewhereInAntarctica(null, null, null, 105.95, -83.28);
}

enum class TestDate(val time: Long) {
    Now(System.currentTimeMillis()),
    HoursAgo(System.currentTimeMillis() - 60 * 60 * 1000L),
    WeekAgo(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
}