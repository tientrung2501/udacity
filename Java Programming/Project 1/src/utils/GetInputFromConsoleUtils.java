package utils;

import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.Room;
import model.enums.RoomType;
import model.interfaces.IRoom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class GetInputFromConsoleUtils {
    private GetInputFromConsoleUtils() {
    }

    private static final Scanner inputScanner = new Scanner(System.in);
    private static final HotelResource HOTEL_RESOURCE = HotelResource.getHotelResource();
    private static final AdminResource ADMIN_RESOURCE = AdminResource.getAdminResource();

    public static String getRoomNumberFromConsole(List<IRoom> roomListOfCurrentState) {
        do {
            System.out.println("Enter room number");
            try {
                int roomNumber = inputScanner.nextInt();
                String roomNumberAsString = String.valueOf(roomNumber);
                if (HOTEL_RESOURCE.getRoom(roomNumberAsString) != null || roomListOfCurrentState.contains(new Room(roomNumberAsString)))
                    System.out.println("Room number exists. Please enter room number again");
                else {
                    return roomNumberAsString;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Invalid number!");
                inputScanner.nextLine();
            }
        } while (true);
    }

    public static double getRoomPriceFromConsole() {
        do {
            System.out.println("Enter price per night");
            try {
                inputScanner.nextLine();
                double price = inputScanner.nextDouble();
                if (price < 0) {
                    System.out.println("Price must be greater than 0");
                } else return price;
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Invalid price!");
                inputScanner.nextLine();
            }
        } while (true);
    }

    public static RoomType getRoomTypeFromConsole() {
        do {
            int roomType;
            try {
                System.out.println("Enter room type: 1 for single bed, 2 for double bed");
                roomType = inputScanner.nextInt();
                switch (roomType) {
                    case 1:
                        return RoomType.SINGLE;
                    case 2:
                        return RoomType.DOUBLE;
                    default:
                        System.out.println("Please choose 1 or 2 only.");
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Invalid room type!");
                inputScanner.nextLine();
            }
        } while (true);
    }

    public static boolean isChooseAnotherOptions(String question) {
        System.out.println(question + ": y/n");
        while (true) {
            String option = inputScanner.nextLine();
            switch (option) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Please choose 'y' or 'n' only.");
            }
        }
    }

    public static Date getCheckInDateFromConsole() {
        System.out.println("Enter CheckIn Date mm/dd/yyyy example 02/01/2020");
        return parseStringToDate();
    }

    public static Date getCheckOutDateFromConsole(Date checkInDate) {
        System.out.println("Enter CheckOut Date mm/dd/yyyy example 02/01/2020");
        Date checkOutDate = parseStringToDate();
        if (checkOutDate.after(checkInDate)) return checkOutDate;
        else {
            System.out.println("Check out date must after check in date");
            return getCheckOutDateFromConsole(checkInDate);
        }
    }

    private static Date parseStringToDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date dateFormatted;
        try {
            String inputDate = inputScanner.next();
            dateFormatted = dateFormat.parse(inputDate);
            return dateFormatted;
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter again");
            return parseStringToDate();
        }
    }

    public static String getEmail() {
        String emailRegex = "^(.+)@(.+).(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        do {
            System.out.println("Enter email format: name@domain.com");
            String email = inputScanner.next();
            if (email == null || !pattern.matcher(email).matches()) {
                System.out.println("Invalid email format");
            } else return email;
        } while (true);
    }

    public static IRoom getRoomForReserve(Collection<IRoom> availableRooms) {
        do {
            System.out.println("What room number would you like to reserve");
            try {
                int roomNumber = inputScanner.nextInt();
                String roomNumberAsString = String.valueOf(roomNumber);
                IRoom room = HOTEL_RESOURCE.getRoom(roomNumberAsString);

                if (room != null && availableRooms.contains(room))
                    return room;
                else System.out.println("Room number does not exists or does not available");

            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Invalid number!");
                inputScanner.nextLine();
            }
        } while (true);
    }

    public static boolean isCustomerExists(String email) {
        try {
            if (ADMIN_RESOURCE.getAllCustomers().isEmpty()) {
                System.out.println("Your account is empty. Please create account first");
            }
            Customer customer = ADMIN_RESOURCE.getCustomer(email);
            if (customer != null) return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
