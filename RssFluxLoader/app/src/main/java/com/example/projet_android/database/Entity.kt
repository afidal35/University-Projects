package com.example.projet_android

import androidx.room.*

// Table for FLUX
@Entity(indices =
    arrayOf(
        Index( value = ["source","url"],unique = true)
    )
)
data class Flux(
    @PrimaryKey(autoGenerate = true)
    var idFlux: Long = 0,
    var source: String,
    var tag: String,
    var url: String
    )
{
    @Ignore
    var checkDownload: Boolean = false
}


// Table for INFO
@Entity(
    //tableName = "Info",
    //primaryKeys = ["idSource"],
    indices = [ Index(value = ["idFlux"]), Index(value = ["link"], unique = true) ],
    foreignKeys = [
        ForeignKey(
            entity = Flux::class,
            parentColumns = ["idFlux"],
            childColumns = ["idFlux"],
            deferred = true,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Info(
    @PrimaryKey(autoGenerate = true)
    var idInfo: Long = 0,

    var idFlux: Long,
    var title: String,
    var description: String,
    var link: String,
    var isNew: Boolean = true
)
{
    @Ignore
    var checkSuppr: Boolean = false
}




