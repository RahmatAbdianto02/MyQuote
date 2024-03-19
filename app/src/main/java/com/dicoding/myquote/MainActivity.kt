package com.dicoding.myquote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.myquote.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpClient.log
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.HttpHeaders
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRandomQoute()

        binding.btnAllQuotes.setOnClickListener{
            startActivity(Intent(this@MainActivity,ListQuotesAcitivity::class.java))
        }

    }

    private fun getRandomQoute() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/random"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int,headers: Array<Header>,responseBody: ByteArray) {
                //jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val  result = String(responseBody)
                log.d(TAG,result)
                try {
                    val responObjects = JSONObject(result)

                    val quote = responObjects.getString("en")
                    val author = responObjects.getString("author")

                    binding.tvQuote.text = quote
                    binding.tvAuthor.text = author
                } catch (e:Exception) {
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode:Int, headers: Array<Header>,responseBody:ByteArray,error: Throwable) {
                //jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode :bad request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity,errorMessage,Toast.LENGTH_SHORT).show()
            }
        })
    }
}