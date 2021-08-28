package io.github.jwgibanez.cartrack.data

import androidx.room.TypeConverter
import io.github.jwgibanez.cartrack.data.model.Address
import io.github.jwgibanez.cartrack.data.model.Company
import io.github.jwgibanez.cartrack.data.model.Geo
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromAddress(address: Address?): String? = address.let {
        Gson().toJson(address)
    }

    @TypeConverter
    fun toAddress(value: String?): Address? = value?.let {
        Gson().fromJson(value, Address::class.java)
    }

    @TypeConverter
    fun fromCompany(company: Company?): String? = company?.let {
        Gson().toJson(company)
    }

    @TypeConverter
    fun toCompany(value: String?): Company? = value?.let {
        Gson().fromJson(value, Company::class.java)
    }

    @TypeConverter
    fun fromGeo(geo: Geo?): String? = geo?.let {
        Gson().toJson(geo)
    }

    @TypeConverter
    fun toGeo(value: String?): Geo? = value?.let {
        Gson().fromJson(value, Geo::class.java)
    }
}