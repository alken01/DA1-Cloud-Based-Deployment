package hotel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BookingManager {

	private Room[] rooms;

	public BookingManager() {
		this.rooms = initializeRooms();
	}

	public Set<Integer> getAllRooms() {
		Set<Integer> allRooms = new HashSet<Integer>();
		Iterable<Room> roomIterator = Arrays.asList(rooms);
		for (Room room : roomIterator) {
			allRooms.add(room.getRoomNumber());
		}
		return allRooms;
	}

	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) {
		//implement this method
		for(Room room : rooms) {
			if(room.getRoomNumber() == roomNumber) {
				for(BookingDetail booking : room.getBookings()) {
					if(booking.getDate().equals(date)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// synchronized to make it thread safe
	public synchronized void addBooking(BookingDetail bookingDetail) {	
		for(Room room : rooms) {
			if(room.getRoomNumber() == bookingDetail.getRoomNumber()) {
				// throw an expection if it is not available
				if(!isRoomAvailable(bookingDetail.getRoomNumber(), bookingDetail.getDate())) {
					throw new IllegalArgumentException("Room is not available\n");
				}
				room.getBookings().add(bookingDetail);
			}
		}

	}

	public Set<Integer> getAvailableRooms(LocalDate date) {
		Set<Integer> availableRooms = new HashSet<Integer>();
		for(Room room : rooms) {
			if(isRoomAvailable(room.getRoomNumber(), date)) {
				availableRooms.add(room.getRoomNumber());
			}
		}
		return availableRooms;
	}

	private static Room[] initializeRooms() {
		Room[] rooms = new Room[4];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(203);
		return rooms;
	}
}
