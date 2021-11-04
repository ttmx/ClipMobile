package com.migueljteixeira.clipmobile.entities

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator

open class StudentCalendar : Entity, Parcelable {
    var name: String? = null
    var date: String? = null
    var hour: String? = null
    var rooms: String? = null
    var number: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        date = `in`.readString()
        hour = `in`.readString()
        rooms = `in`.readString()
        number = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(date)
        dest.writeString(hour)
        dest.writeString(rooms)
        dest.writeString(number)
    }

//    companion object {
//        val CREATOR: Creator<StudentCalendar> = object : Creator<StudentCalendar> {
//            override fun createFromParcel(`in`: Parcel): StudentCalendar? {
//                return StudentCalendar(`in`)
//            }
//
//            override fun newArray(size: Int): Array<StudentCalendar?> {
//                return arrayOfNulls(size)
//            }
//        }
//    }

    companion object CREATOR : Creator<StudentCalendar> {
        override fun createFromParcel(parcel: Parcel): StudentCalendar {
            return StudentCalendar(parcel)
        }

        override fun newArray(size: Int): Array<StudentCalendar?> {
            return arrayOfNulls(size)
        }
    }
}