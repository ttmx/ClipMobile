package com.migueljteixeira.clipmobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentCalendar extends Entity implements Parcelable {
    private String name, date, hour, rooms, number;

    public StudentCalendar() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getHour() { return hour; }
    public void setHour(String hour) { this.hour = hour; }

    public String getRooms() { return rooms; }
    public void setRooms(String rooms) { this.rooms = rooms; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    protected StudentCalendar(Parcel in) {
        name = in.readString();
        date = in.readString();
        hour = in.readString();
        rooms = in.readString();
        number = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(hour);
        dest.writeString(rooms);
        dest.writeString(number);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StudentCalendar> CREATOR = new Parcelable.Creator<StudentCalendar>() {
        @Override
        public StudentCalendar createFromParcel(Parcel in) {
            return new StudentCalendar(in);
        }

        @Override
        public StudentCalendar[] newArray(int size) {
            return new StudentCalendar[size];
        }
    };


}
