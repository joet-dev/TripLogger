package com.example.csc202assignment.database

import androidx.room.*
import java.util.*

/**
 *  TripTypeConverter class details how Room should convert the trip data-types for storage and retrieval.
 *
 *  @author Joseph Thurlow
 */
class TripTypeConverter {

    // Type converter for date.
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    // Type converter for UUID.
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}