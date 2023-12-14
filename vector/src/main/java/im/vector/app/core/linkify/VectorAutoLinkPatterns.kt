
package im.vector.app.core.linkify


object VectorAutoLinkPatterns {

    
    private const val LAT_OR_LONG_OR_ALT_NUMBER = "-?\\d+(?:\\.\\d+)?"
    private const val COORDINATE_SYSTEM = ";crs=[\\w-]+"

    val GEO_URI: Regex = Regex("(?:geo:)?" +
            "(" + LAT_OR_LONG_OR_ALT_NUMBER + ")" +
            "," +
            "(" + LAT_OR_LONG_OR_ALT_NUMBER + ")" +
            "(?:" + "," + LAT_OR_LONG_OR_ALT_NUMBER + ")?" + 
            "(?:" + COORDINATE_SYSTEM + ")?" +
            "(?:" + ";u=\\d+(?:\\.\\d+)?" + ")?" + 
            "(?:" +
            ";[\\w-]+=(?:[\\w-_.!~*'()]|%[\\da-f][\\da-f])+" + 
            ")*", RegexOption.IGNORE_CASE)
}
