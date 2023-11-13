package ui;

import api.AdminResource;
import model.Customer;
import model.FreeRoom;
import model.Room;
import model.enums.RoomType;
import model.interfaces.IRoom;

import java.util.*;

import static utils.GetInputFromConsoleUtils.*;

public class AdminMenu {
    private static final AdminResource adminResource = AdminResource.getAdminResource();
    private static final Scanner inputScanner = new Scanner(System.in);

    public static void displayAdminMenu() {
        int actionNumber = 0;
        do {
            try {
                displayAdminMenuOption();
                actionNumber = inputScanner.nextInt();
                handleByActionNumber(actionNumber);
            } catch (Exception e) {
                System.out.println("Invalid action number. Please try again");
                inputScanner.nextLine();
                displayAdminMenu();
            }
        } while (actionNumber >= 1 && actionNumber <= 4);
    }

    private static void handleByActionNumber(int actionNumber) {
        switch (actionNumber) {
            case 1:
                seeAllCustomers();
                break;
            case 2:
                seeAllRooms();
                break;
            case 3:
                seeAllReservations();
                break;
            case 4:
                addRoom();
                break;
            case 5:
                MainMenu.displayMainMenu();
                break;
            default:
                System.out.println("Invalid action number. Please choose again!");
        }
    }

    private static void addRoom() {
        List<IRoom> rooms = new ArrayList<>();
        try {
            do {
                String roomNumber = getRoomNumberFromConsole(rooms);
                double roomPrice = getRoomPriceFromConsole();
                RoomType roomType = getRoomTypeFromConsole();
                if (roomPrice > 0) rooms.add(new Room(roomNumber, roomPrice, roomType));
                else rooms.add(new FreeRoom(roomNumber, roomType));
            } while (isChooseAnotherOptions("Would you like to add another room"));
            adminResource.addRoom(rooms);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void seeAllRooms() {
        adminResource.getAllRooms().forEach(room -> System.out.println(room.toString()));
    }

    private static void seeAllCustomers() {
        Collection<Customer> customers = adminResource.getAllCustomers();
        if (customers.isEmpty()) System.out.println("There are no customer exists!");
        else customers.forEach(customer -> System.out.println(customer.toString()));
    }

    private static void seeAllReservations() {
        adminResource.displayAllReservations();
    }

    private static void displayAdminMenuOption() {
        System.out.println("Admin Menu");
        System.out.println("-----------------------------------------");
        System.out.println(
                "1. See all Customers\n" +
                        "2. See all Rooms\n" +
                        "3. See all Reservations\n" +
                        "4. Add a Room\n" +
                        "5. Back to Main Menu");
        System.out.println("-----------------------------------------");
        System.out.println("Please select a number for the menu option");

    }

    private AdminMenu() {
    }
}
