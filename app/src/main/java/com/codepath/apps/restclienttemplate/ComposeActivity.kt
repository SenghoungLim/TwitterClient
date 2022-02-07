package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText //Declare a var without initialize until use later
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient //Declare client so we can use client to call tweet method
                                        //in twitterClient.kt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener {
            //What happen when click?
            //1. Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString()
                //i. Check empty tweet
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Empty Tweets are not allowed", Toast.LENGTH_SHORT)
                    .show()
                //Another way to make a text appear is using SnackBar message where you can set
                //a particular second to make it disappear
            }
                //ii. Check characters limit
            if (tweetContent.length > 140){
                Toast.makeText(this, "The tweet is too long, limit is 140 characters", Toast.LENGTH_SHORT)
                    .show()
            }else{
                //2. Make an API call to Twitter to publish tweet
                client.publishTweet(tweetContent, object:JsonHttpResponseHandler(){
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        //If success, send to twitter Timeline Activity
                        Log.e(TAG, "Successfully tweeted")
                        val tweet = Tweet.fromJson(json.jsonObject) //get the tweet content and turn
                        //to Json

                        //Pass back to timeline activity
                        val intent = Intent()
                        intent.putExtra("tweet", tweet) //Since tweet is our own created obj
                        //Android cannot be handle, so we need to turn it to parsable obj so android
                        //can pass it to another actvity (Compose -> Timeline act)
                        //1. Add pluggin in buidl.gradle(app) to be able to parse our own created obj
                        setResult(RESULT_OK, intent)
                        finish() //Close the compose activity and back to timeline
                    }

                })
            }

        }
    }
    companion object{
        val TAG = "ComposeActivity"
    }
}