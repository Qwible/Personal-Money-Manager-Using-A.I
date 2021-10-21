package com.example.qwibBank.Misc

import com.example.qwibBank.Entities.Transaction

abstract class ListItem {

    abstract val type: Int

    companion object {
        val TYPE_HEADER = 0
        val TYPE_TRANSACTION = 1
    }
}

class HeaderItem : ListItem() {

    var date: String? = null

    // here getters and setters
    // for title and so on, built
    // using date

    override val type: Int
        get() = TYPE_HEADER

}

class TransactionItem : ListItem() {

    var transaction: Transaction? = null

    // here getters and setters
    // for title and so on, built
    // using event

    override val type: Int
        get() = TYPE_TRANSACTION

}