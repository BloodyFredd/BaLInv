package com.example.bali

class item{
    var id: String? = null
    var name: String? = null
    var amount: Int = 0
    var price: Int = 0
    var changedate: String? = null
    var worker: String? = null


    constructor() {}
    constructor(id: String?, name: String?, amount: Int, price: Int,changedate: String?,worker: String?) {
        this.id = id
        this.name = name
        this.amount = amount
        this.price = price
        this.changedate = changedate
        this.worker = worker
    }

    override fun toString(): String {
        return "Customer [id=" + id + ", name=" + name + ", amount=" + amount + ", price=" + price + ", changedate=" + changedate + ", worker=" + worker +"]"
    }
}