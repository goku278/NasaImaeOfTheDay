package com.example.daggerexamplemvvm.model

class Items {
    var name: String? = null
    var description: String? = null
    var owner: Owner? = null

    constructor(name: String?, description: String?, owner: Owner?) {
        this.name = name
        this.description = description
        this.owner = owner
    }


    override fun toString(): String {
        return "Items(name=$name, description=$description, owner=$owner)"
    }


}