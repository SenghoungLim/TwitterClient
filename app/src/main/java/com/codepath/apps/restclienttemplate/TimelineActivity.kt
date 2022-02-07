package com.codepath.apps.restclienttemplate

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient //declare the client var, an instance of the client class
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this) //initial the client class
        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            //Log.i(TAG, "Refreshing timeline")
            populateHomeTimeline()
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

    //Get Recycle view
        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        //call populateHomeTimeline once an activity launch
        //It calls the getHomeTimeline then success or not it call depending on the method
        populateHomeTimeline()
    }
    //Initialize the menu instance
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate the menu so it shows in the bar
        menuInflater.inflate(R.menu.menu_main, menu)
        return true //Return true so the menu can be created
    }

    //What happen once a user click on the menu options item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose)
        {
            //Test if the compose click works, once clicks it should show the tweet below
                //Toast.makeText(this, "Ready to compose a tweet", Toast.LENGTH_SHORT).show()
            //After test, we can navigate to compose screen/activity by using intent
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        return super.onOptionsItemSelected(item)
    }
    // In ComposeActivity, once the tweet content is parsed and finished(), it needs
    // to send the content back to Timeline activity
    //so, we need a function to handle that
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            //Getting data from our intent
            val tweet = data?.getParcelableExtra("tweet") as Tweet
            //Update the timeline
            //Modifying the data source of tweets
            tweets.add(0, tweet)
            //Update adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)


        }
        super.onActivityResult(requestCode, resultCode, data)//At the end, we need to let
        //android handle whatever happens in the onActivityResult
    }

    fun populateHomeTimeline() //utilize the TwitterClient where we write to get the timeline
    //This make the get timeline work
    {
        client.getHomeTimeline(object: JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess!")
                //Get the Json Array
                try {
                    adapter.clear() // Clear the current tweet
                    val jsonArray = json.jsonArray
                    //Convert the json array to a list of tweet
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    //Add a list of tweets
                    tweets.addAll(listOfNewTweetsRetrieved)
                    //Notify the adapter
                    adapter.notifyDataSetChanged()
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }
            }
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure!")
            }
        }
        )
    }
    companion object{
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 10 //Request code tells us/identify when the return results arrive
        //We can have multiple results so this would help distinguish one result from another
    }
}