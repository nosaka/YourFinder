package me.ns.yourfinder.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by shintaro.nosaka on 2017/09/08.
 */
@Entity
class Finder {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var name: String? = null
    var description: String? = null
    var position: Int? = null

    var x: Float = 0f
    var y: Float = 0f
    var init: Boolean = true
}