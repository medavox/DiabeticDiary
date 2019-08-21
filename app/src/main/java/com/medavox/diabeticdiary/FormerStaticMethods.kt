package com.medavox.diabeticdiary

import android.widget.Adapter
import java.util.regex.Pattern

/**
 * @author Adam Howard
@since 2019-08-21
 */
//todo: improve validation to include international numbers
fun isValidPhoneNumber(s: String): Boolean {
    val pati = Pattern.compile("(0|\\+44)7[0-9]{9}")
    return pati.matcher(s).matches()
    //return s.length() == 11 && s.startsWith("07");
}

fun stringsOf(adapter: Adapter):String {
    val sb = StringBuilder("[ ")
    for(i in 0 until adapter.count) {
        sb.append(adapter.getItem(i).toString()).append("; ")
    }
    return sb.append(" ]").toString()
}