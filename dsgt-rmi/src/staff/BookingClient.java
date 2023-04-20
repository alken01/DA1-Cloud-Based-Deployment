package staff;

import java.time.LocalDate;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManager;

public class BookingClient extends AbstractScriptedSimpleTest {

	private BookingManager bm = null;

	public static void main(String[] args) throws Exception {
		BookingClient client = new BookingClient();
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/
	public BookingClient() {
		try {
			//Look up the registered remote instance
			bm = new BookingManager();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) {
		//Implement this method
		return bm.isRoomAvailable(roomNumber, date);
	}

	@Override
	public void addBooking(BookingDetail bookingDetail) throws Exception {
		//Implement this method throw and catch exception
		try{
			bm.addBooking(bookingDetail);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) {
		//Implement this method 
		return bm.getAvailableRooms(date);
	}

	@Override
	public Set<Integer> getAllRooms() {
		return bm.getAllRooms();
	}
}
