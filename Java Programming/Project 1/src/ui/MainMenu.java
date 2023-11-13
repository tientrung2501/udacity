package ui;

import api.AdminResource;
import api.HotelResource;
import model.Reservation;
import model.interfaces.IRoom;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import static utils.GetInputFromConsoleUtils.*;

public class MainMenu {
    private static final HotelResource hotelResource = HotelResource.getHotelResource();
    private static final AdminResource adminResource = AdminResource.getAdminResource();
    private static final Scanner inputScanner = new Scanner(System.in);

    private MainMenu() {
    }

    public static void displayMainMenu() {
        int actionNumber = 0;
        do {
            try {
                displayWelcomeContent();
                actionNumber = inputScanner.nextInt();
                handleByActionNumber(actionNumber);
            } catch (Exception e) {
                System.out.println("Invalid action number. Please try again");
                inputScanner.nextLine();
                displayMainMenu();
            }
        } while (actionNumber >= 1 && actionNumber <= 3);
    }

    private static void displayWelcomeContent() {
        System.out.println("Welcome to the Hotel Reservation Application");
        System.out.println("-----------------------------------------");
        System.out.println(
                "1. Find and reserve a rom\n" +
                        "2. See my reservations\n" +
                        "3. Create an account\n" +
                        "4. Admin\n" +
                        "5. Exit");
        System.out.println("-----------------------------------------");
        System.out.println("Please select a number for the menu option");
    }

    private static void handleByActionNumber(int actionNumber) {
        switch (actionNumber) {
            case 1:
                findAndReserveARoom();
                break;
            case 2:
                seeMyReservations();
                break;
            case 3:
                createAnAccount();
                break;
            case 4:
                AdminMenu.displayAdminMenu();
                break;
            case 5:
                break;
            default:
                System.out.println("Invalid action number. Please choose again!");
        }
    }

    private static void createAnAccount() {
        try {
            String email = getEmail();
            System.out.println("First Name");
            String firstName = inputScanner.nextLine();
            System.out.println("Last Name");
            String lastName = inputScanner.nextLine();
            hotelResource.createACustomer(email, firstName, lastName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter again!");
            createAnAccount();
        }
    }

    private static void findAndReserveARoom() {
        Date checkInDate = getCheckInDateFromConsole();
        Date checkOutDate = getCheckOutDateFromConsole(checkInDate);
        try {
            Collection<IRoom> availableRooms = findRoom(checkInDate, checkOutDate);
            if (availableRooms.isEmpty()) {
                System.out.println("There no room for you right new. Recommendation rooms from " +
                        checkInDate + " to " + checkOutDate + ": ");
                checkInDate = Date.from(checkInDate.toInstant().plus(7, ChronoUnit.DAYS));
                checkOutDate = Date.from(checkOutDate.toInstant().plus(7, ChronoUnit.DAYS));
                availableRooms = findRoom(checkInDate, checkOutDate);
                if (availableRooms.isEmpty()) {
                    System.out.println("There are no room available!");
                    return;
                }
            }

            reserveARoom(availableRooms, checkInDate, checkOutDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveARoom(Collection<IRoom> availableRooms, Date checkInDate, Date checkOutDate){
        if (isChooseAnotherOptions("Would you like to book a room?") && !availableRooms.isEmpty()) {
            if (isChooseAnotherOptions("Do you have an account with us?") && !adminResource.getAllCustomers().isEmpty()) {
                String customerEmail;
                do {
                    customerEmail = getEmail();
                } while (!isCustomerExists(customerEmail));
                IRoom room = getRoomForReserve(availableRooms);

                Reservation reservation = hotelResource.bookARoom(customerEmail, room, checkInDate, checkOutDate);
                System.out.println(reservation.toString());
            } else {
                System.out.println("Please create account first.");
                displayMainMenu();
            }
        } else displayMainMenu();
    }

    private static Collection<IRoom> findRoom(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> availableRooms = hotelResource.findARoom(checkInDate, checkOutDate);
        if (availableRooms.isEmpty()) {
           return Collections.emptyList();
        }
        availableRooms.forEach(room -> System.out.println(room.toString()));
        return availableRooms;
    }

    private static void seeMyReservations() {
        String email = getEmail();
        Collection<Reservation> reservations = hotelResource.getCustomersReservations(email);
        if (reservations.isEmpty()) System.out.println("Your reservations is empty");
        else reservations.forEach(reservation -> System.out.println(reservation));
    }
}
