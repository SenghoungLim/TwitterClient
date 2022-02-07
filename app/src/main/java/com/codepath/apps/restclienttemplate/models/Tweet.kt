package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject


@Parcelize

class Tweet (var body: String = "",
             var createdAt: String = "",
             var user: User? = null): Parcelable {
    /*var body: String = ""
    var createdAt: String = ""
    var user: User? = null*/
    //Clear the variables above and declare it in constructor instead so it can be parcelable
    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.user =  User.fromJson(jsonObject.getJSONObject("user"))
            return tweet
        }

        //Get json file to a list of tweet because we not parsing 1 data
        fun fromJsonArray(jsonArray: JSONArray): List<Tweet>{
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length())
            {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}