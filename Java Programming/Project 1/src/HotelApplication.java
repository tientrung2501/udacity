import api.AdminResource;
import api.HotelResource;
import model.FreeRoom;
import model.Room;
import model.enums.RoomType;
import model.interfaces.IRoom;
import ui.MainMenu;

import java.util.ArrayList;
import java.util.List;

public class HotelApplication {
    public static void main(String[] args) {
        //Add test data
        List<IRoom> rooms = new ArrayList<>();
        rooms.add(new Room("1", 10.0, RoomType.SINGLE));
        rooms.add(new FreeRoom("2",  RoomType.DOUBLE));
        AdminResource.getAdminResource().addRoom(rooms);
        HotelResource.getHotelResource().createACustomer("admin@gmail.com", "Admin", "User");

        MainMenu.displayMainMenu();
    }
}