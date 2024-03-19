package com.dicoding.myquote

import android.icu.lang.UCharacter.JoiningGroup.TAH
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myquote.databinding.ActivityListQuotesAcitivityBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpClient.log
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListQuotesAcitivity : AppCompatActivity() {

    companion object {
        private val TAG = ListQuotesAcitivity::class.java.simpleName
    }
    private lateinit var binding: ActivityListQuotesAcitivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.listQuotes.setLayoutManager(layoutManager)
        val  itemDecoration = DividerItemDecoration(this,layoutManager.orientation)
        binding.listQuotes.addItemDecoration(itemDecoration)

        getListQuotes()
    }
    private fun getListQuotes() {
        binding.progressBar.visibility = View.VISIBLE
        val  client = AsyncHttpClient()
        val  url = "https://quote-api.dicoding.dev/list"
        client.get(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCcde: Int,headers: Array<Header>,responBody: ByteArray) {
                //jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val listQuote = ArrayList<String>()
                val  result = String(responBody)
                log.d(TAG,result)
                try {
                    val jsonArray = JSONArray(result)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val  quote = jsonObject.getString("en")
                        val  author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n — $author\n")
                    }

                    val adapter = QuoteAdapter(listQuote)
                    binding.listQuotes.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesAcitivity,e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun  onFailure(statusCode: Int,header:Array<Header>,responBody: ByteArray,error:Throwable) {
                //jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val  errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@ListQuotesAcitivity,errorMessage,Toast.LENGTH_SHORT).show()
            }
        })
    }
}