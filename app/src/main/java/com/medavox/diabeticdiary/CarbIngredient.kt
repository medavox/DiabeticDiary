package com.medavox.diabeticdiary

/**
 * @author Adam Howard
 * @since 01/09/2017
 */
data class CarbIngredient(val grams:Int, val percentCarb:Int, val name:String?=null) {

    /**Calculate and return the total CP in this ingredient*/
    val CPx1000:Int get() {
        return percentCarb * grams
    }

    override fun toString():String {
        return "CarbIngredient ["+(if(name == null)  "" else "\"$name\" ")+grams+"g, "+percentCarb+"%]"
    }
}
