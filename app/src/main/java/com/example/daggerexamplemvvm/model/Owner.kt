package com.example.daggerexamplemvvm.model

class Owner {
    var avatar_url: String? = null

    constructor(avatar_url: String?) {
        this.avatar_url = avatar_url
    }

    override fun toString(): String {
        return "Owner(avatar_url=$avatar_url)"
    }


}