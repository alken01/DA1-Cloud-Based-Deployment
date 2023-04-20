package rental;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalCompany implements ICarRentalCompany {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<Car> cars){
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
	}

	/********
	 * NAME *
	 ********/

	@Override
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/*************
	 * CAR TYPES *
	 *************/

	public Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}

	public CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}

	@Override
	public boolean isFree(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName))
			return getFreeCarTypes(start, end).contains(carTypes.get(carTypeName));
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}

	@Override
	public Set<CarType> getFreeCarTypes(Date start, Date end) {
		Set<CarType> freeCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isFree(start, end)) {
				freeCarTypes.add(car.getType());
			}
		}
		return freeCarTypes;
	}

	/*********
	 * CARS *
	 *********/

	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}

	private List<Car> getFreeCars(String carType, Date start, Date end) {
		List<Car> freeCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isFree(start, end)) {
				freeCars.add(car);
			}
		}
		return freeCars;
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
				new Object[]{name, client, constraints.toString()});

		CarType type = getCarType(constraints.getCarType());

		if(!isFree(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
					+ "> No cars available to satisfy the given constraints.");

		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());

		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
				/ (1000 * 60 * 60 * 24D));
	}

	public Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> freecars = getFreeCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(freecars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
			+ " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = freecars.get((int)(Math.random()*freecars.size()));

		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
}
