package com.migueljteixeira.clipmobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentScheduleClass extends Entity implements Parcelable {
    private String name, name_min, type, hour_start, hour_end, room;

    public StudentScheduleClass() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name;}

    public String getNameMin() { return name_min; }
    public void setNameMin(String name_min) { this.name_min = name_min; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getHourStart() { return hour_start; }
    public void setHourStart(String hour_start) { this.hour_start = hour_start; }

    public String getHourEnd() { return hour_end; }
    public void setHourEnd(String hour_end) { this.hour_end = hour_end; }

    public String getRoom() { return room; }
    public void setRoom(String room) {this.room = room; }

    protected StudentScheduleClass(Parcel in) {
        name = in.readString();
        name_min = in.readString();
        type = in.readString();
        hour_start = in.readString();
        hour_end = in.readString();
        room = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(name_min);
        dest.writeString(type);
        dest.writeString(hour_start);
        dest.writeString(hour_end);
        dest.writeString(room);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StudentScheduleClass> CREATOR = new Parcelable.Creator<StudentScheduleClass>() {
        @Override
        public StudentScheduleClass createFromParcel(Parcel in) {
            return new StudentScheduleClass(in);
        }

        @Override
        public StudentScheduleClass[] newArray(int size) {
            return new StudentScheduleClass[size];
        }
    };

}
