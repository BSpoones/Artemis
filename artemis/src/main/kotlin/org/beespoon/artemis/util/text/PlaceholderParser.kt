package org.beespoon.artemis.util.text

@JvmName("NullStringParse")
fun String?.parse(vararg placeholders: Any): String? {
    if (this == null) return null
    return PlaceholderParser.parse(this, *placeholders)
}

@JvmName("StringParse")
fun String.parse(vararg placeholders: Any): String = PlaceholderParser.parse(this, *placeholders)

fun parse(input: String, vararg placeholders: Any) = PlaceholderParser.parse(input, *placeholders)

object PlaceholderParser {

    private val SURROUNDING_CHARS = "<" to ">"

    fun parse(text: String, vararg placeholders: Any): String {
        if (placeholders.isEmpty()) return text
        assert(placeholders.size % 2 == 0) { "Placeholders must be in a key-value pair!" }

        var output = text

        placeholders.toList().zipWithNext().forEach { (key, value) ->
            assert(key is String) { "Placeholder key must be a string!" }
            output = output.replace("${SURROUNDING_CHARS.first}$key${SURROUNDING_CHARS.second}", value.toString())
        }
        return output
    }
}