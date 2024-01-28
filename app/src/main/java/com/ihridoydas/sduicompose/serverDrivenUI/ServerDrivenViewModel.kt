package com.ihridoydas.sduicompose.serverDrivenUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

@HiltViewModel
class ServerDrivenViewModel : ViewModel(){
    private val realtimeDatabase = Firebase.database
    private val dataNode = realtimeDatabase.getReference("ui/data")
    private val layoutNode = realtimeDatabase.getReference("ui/layout")
    private val metaNode = realtimeDatabase.getReference("ui/meta")


    //Firebase models
    data class NewsItem(
        val id: String = "",
        val title: String = "",
        val description: String = "",
        val isFavorite: Boolean = false,
    )

    data class Meta(
        val canFavorite: Boolean = false,
        val mode: String = "",
    )

    //Firebase Flow
    private val _dataFlow: Flow<List<NewsItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newsItems = snapshot.children.map {
                    it.getValue<NewsItem>()!!
                        .copy(
                            isFavorite = it.children.find {
                                it.key == "isFavorite"
                            }!!.getValue<String>()!!.run {
                                return@run this == "true"
                            })
                }
                trySend(newsItems)
            }

            override fun onCancelled(error: DatabaseError) {
                // Not worrying about this for the demo
            }

        }
        dataNode.addValueEventListener(listener)
        awaitClose {dataNode.removeEventListener(listener)}
    }
    private val _layoutFlow: Flow<Map<String,LayoutType>> = callbackFlow {
        fun parse(snapshot: DataSnapshot) : LayoutType {
            val type = snapshot.children.find {
                it.key == "type"
            }!!.getValue<String>()!!
            return when (type){
                "list" -> LayoutType.List
                "gird" -> LayoutType.Grid(
                    columns = snapshot.children.find { it.key == "columns" }!!.getValue<Int>()!!,
                )
                else -> {
                    Timber.tag("Unknown type").e(type)
                    LayoutType.List
                }
            }
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.children.associate {
                    it.key!! to parse(it)
                }
                trySend(map)
            }

            override fun onCancelled(error: DatabaseError) {
                // Not worrying about this for the demo
            }
        }
        layoutNode.addValueEventListener(listener)
        awaitClose {layoutNode.removeEventListener(listener)}
    }
    private val _metaFlow: Flow<Meta> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               trySend(snapshot.getValue<Meta>()!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Not worrying about this for the demo
            }

        }
        metaNode.addValueEventListener(listener)
        awaitClose {metaNode.removeEventListener(listener)}
    }

    //UI Flow
    val layoutInformationFlow: StateFlow<LayoutInformation?> = combine(
        _dataFlow, _layoutFlow, _metaFlow,
    ){newItems,layoutTypeMap,meta ->
        if (newItems.isEmpty()) return@combine null
        val layoutMetaInformation = LayoutMeta(
            layoutType = layoutTypeMap[meta.mode] ?: LayoutType.List,
            favoriteEnabled = meta.canFavorite,
        )
        return@combine LayoutInformation(
            layoutMeta = layoutMetaInformation,
            layoutData = newItems,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)
}
