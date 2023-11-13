package service;

import model.Customer;
import model.Reservation;
import model.interfaces.IRoom;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {
    private static final ReservationService RESERVATION_SERVICE = new ReservationService();
    public static final Map<String, IRoom> ROOMS = new HashMap<>();
    public static final Set<Reservation> RESERVATIONS = new HashSet<>();

    private ReservationService() {}

    public static ReservationService getReservationService() {
        return RESERVATION_SERVICE;
    }

    public void addRoom(IRoom room) {
        if (room.getRoomNumber().isEmpty()) throw new IllegalArgumentException("Room number is required");
        if (ROOMS.containsKey(room.getRoomNumber()))
            throw new IllegalArgumentException("Room number must be unique");
        ROOMS.put(room.getRoomNumber(), room);
        System.out.println(room);
    }

    public IRoom getARoom(String roomId) {
        if (roomId.isEmpty()) throw new IllegalArgumentException("Room number is required");
        return ROOMS.get(roomId);
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        try {
            if (checkInDate.after(checkOutDate)) throw new IllegalArgumentException("Invalid check in or check out date");
            Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
            if (RESERVATIONS.contains(reservation))
                throw new IllegalArgumentException("This room has been reserved");
            RESERVATIONS.add(reservation);
            return reservation;
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        if (RESERVATIONS.isEmpty()) return ROOMS.values();
        if (checkInDate == null || checkOutDate == null) throw new IllegalArgumentException("Invalid date");
        Collection<IRoom> availableRoom = getAvailableRoom(checkInDate, checkOutDate);
        if (availableRoom.isEmpty())
            return Collections.emptyList();
        else return availableRoom;
    }

    private Collection<IRoom> getAvailableRoom(Date checkInDate, Date checkOutDate) {
        try {
            Collection<Reservation> reservations = RESERVATIONS.stream().filter(reservation ->
                    reservation.getCheckInDate().before(checkOutDate) && reservation.getCheckOutDate().after(checkInDate)
            ).collect(Collectors.toList());
            Collection<IRoom> reservationsRoom = reservations.stream().map(Reservation::getRoom).collect(Collectors.toList());
            return ROOMS.values().stream().filter(room -> !reservationsRoom.contains(room)).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Collection<Reservation> getCustomersReservation(Customer customer) {
        if (customer == null || customer.getEmail() == null) throw new IllegalArgumentException("Invalid customer");
        return RESERVATIONS.stream().filter(r -> r.getCustomer().equals(customer)).collect(Collectors.toList());
    }

    public void printAllReservation() {
        if (RESERVATIONS.isEmpty()) System.out.println("Your reservation is empty");
        else RESERVATIONS.forEach(r -> System.out.println(r.toString()));
    }
}
