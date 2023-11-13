package model;

import model.enums.RoomType;

public class FreeRoom extends Room {
    public FreeRoom(String roomNumber, RoomType enumeration) {
        super(roomNumber, 0.0, enumeration);
    }

    @Override
    public String toString() {
        return
                "Room number: '" + getRoomNumber() + '\'' +
                ", Price: $0" +
                ", Type of room: " + getRoomType().toString();
    }
}
