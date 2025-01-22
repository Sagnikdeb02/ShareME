package com.example.shareme.firebase

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

//@HiltViewModel
class SendDataViewModel: ViewModel() {
    private val fireBaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channal>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getChannel()
    }

    private fun getChannel(){
        fireBaseDatabase.getReference("channel").get().addOnSuccessListener {
            val list = mutableListOf<Channal>()
            it.children.forEach{ data->
                val channel = Channal(data.key!!, data.value.toString(), data.value.toString())
                list.add(channel)
            }
            _channels.value = list
        }
    }
}