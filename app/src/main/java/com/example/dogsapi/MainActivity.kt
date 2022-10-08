package com.example.dogsapi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsapi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity(), OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchviewDogs.setOnQueryTextListener(this)
        initRecycleView()
    }

    private fun initRecycleView() {
        adapter = DogAdapter(dogImages)
        binding.rcwDogs.layoutManager = LinearLayoutManager(this)
        binding.rcwDogs.adapter = adapter
    }


    private fun getRetrofit() : Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByName(query:String) {
       CoroutineScope(Dispatchers.IO).launch {
           val call = getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")
           val puppies = call.body()

           runOnUiThread {
               if(call.isSuccessful){
                   // Show Recycler View
                   val images = puppies?.images ?: emptyList()
                   dogImages.clear()
                   dogImages.addAll(images)
                   adapter.notifyDataSetChanged()
               }else{
                   //Show Error
                   showError()
               }
           }

       }
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken,0)
    }
    
    
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchByName(query.lowercase())
        }
        hideKeyboard()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}