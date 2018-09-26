package com.example.asus.halalfoodfinder;

public class ReminderPlace {

    public String placeName;
    public String placeAdress;

    public ReminderPlace(String placeName, String placeAdress) {
        this.placeName = placeName;
        this.placeAdress = placeAdress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAdress() {
        return placeAdress;
    }
}
