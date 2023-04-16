package com.example.csc202assignment

import androidx.room.*
import java.util.*

/**
 * Trip is a data class that defines the structure of a table in the database.
 * The annotation is used to indicate this.
 *
 * @author Joseph Thurlow
 */
@Entity
data class Trip(@PrimaryKey val id: UUID = UUID.randomUUID(),
                var title: String = "",
                var destination: String = "",
                var date: Date = Date(),
                var duration: String = "") {

    val imageFileName
        get() = "IMG_$id.jpg"
}