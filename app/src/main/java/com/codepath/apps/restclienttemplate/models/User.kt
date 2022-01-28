package com.codepath.apps.restclienttemplate.models

import org.json.JSONObject

class User {
    var name: String = ""
    var screenName: String =""
    var publicImageUrl: String = ""

    //Reference without creating new instances of the user obj
    companion object
    {
        //method that parse JSON obj and convert to user obj
        fun fromJson(jsonObject: JSONObject): User{
            val user = User()
            user.name = jsonObject.getString("name")
            user.screenName = jsonObject.getString("screen_name")
            user.publicImageUrl = jsonObject.getString("profile_image_url_https")

            return user
        }
    }
}