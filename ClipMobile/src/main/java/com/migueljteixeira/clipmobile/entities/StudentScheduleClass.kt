package com.migueljteixeira.clipmobile.entities

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass

open class StudentScheduleClass : Entity, Parcelable {
    lateinit var name: String
    lateinit var nameMin: String
    lateinit var type: String
    lateinit var hourStart: String
    lateinit var hourEnd: String
    lateinit var room: String

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()!!
        nameMin = `in`.readString()!!
        type = `in`.readString()!!
        hourStart = `in`.readString()!!
        hourEnd = `in`.readString()!!
        room = `in`.readString()!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(nameMin)
        dest.writeString(type)
        dest.writeString(hourStart)
        dest.writeString(hourEnd)
        dest.writeString(room)
    }

//    companion object {
//        val CREATOR: Creator<StudentScheduleClass> = object : Creator<StudentScheduleClass> {
//            override fun createFromParcel(`in`: Parcel): StudentScheduleClass {
//                return StudentScheduleClass(`in`)
//            }
//
//            override fun newArray(size: Int): Array<StudentScheduleClass?> {
//                return arrayOfNulls(size)
//            }
//        }
//    }

    companion object CREATOR : Creator<StudentScheduleClass> {
        override fun createFromParcel(parcel: Parcel): StudentScheduleClass {
            return StudentScheduleClass(parcel)
        }

        override fun newArray(size: Int): Array<StudentScheduleClass?> {
            return arrayOfNulls(size)
        }
    }
}