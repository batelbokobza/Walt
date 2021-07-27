package com.walt;

import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.exception.LessDeliveryDetailsException;
import com.walt.model.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.walt.exception.DriverLessException;
import com.walt.exception.NotSameCityException;

@Service
public class WaltServiceImpl implements WaltService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd 'at' HH:mm:ss");
    DriverRepository driverRepository;
    DeliveryRepository deliveryRepository;
    Delivery delivery;
    Driver driver;

    @Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime){
        try {
            // If the shipping details are incomplete, or the driver / customer can not place the order.
            checkOrderAndAssignDriver(customer, restaurant, deliveryTime);
        } catch (DriverLessException | NotSameCityException | LessDeliveryDetailsException e) {
            return null;
        }
        delivery = new Delivery(driver, restaurant, customer, deliveryTime);
        deliveryRepository.save(delivery);
        updateNewDeliveryToDriver();
        System.out.println(toStringDeliveryInformation(delivery));
        return delivery;
    }

    private void checkOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) throws NotSameCityException, DriverLessException, LessDeliveryDetailsException {
        if (customer.getName() == null || customer.getCity() == null || customer.getAddress() == null) {
            throw new LessDeliveryDetailsException();
        }
        else if (restaurant.getName() == null || restaurant.getCity() == null || restaurant.getAddress() == null) {
            throw new LessDeliveryDetailsException();
        }
        else if(!customer.getCity().getName().equals(restaurant.getCity().getName())){
            throw new NotSameCityException(customer.getCity(),restaurant.getCity());
        }
        driver = searchDriverLeastBusy(restaurant.getCity(), deliveryTime);
        if(driver == null){
            throw new DriverLessException(restaurant.getCity(), dateFormat.format(deliveryTime));
        }
    }

    private Driver searchDriverLeastBusy(City city, Date deliveryTime){
        List<Driver> driversByCity = driverRepository.findAllDriversByCity(city);
        Driver selectedDriver = null; // If no available driver is found, null will be returned.
        int minDeliveries = Integer.MAX_VALUE;
        for(Driver d: driversByCity) {
            if (d.isAvailableToDelivery(deliveryTime)) {
                if (minDeliveries > d.getDeliveriesNumber()) {
                    minDeliveries = d.getDeliveriesNumber();
                    selectedDriver = d;
                }
            }
        }
        return selectedDriver;
    }

    private void updateNewDeliveryToDriver(){
        double distance = getDistance();
        delivery.setDistance(distance);
        driver.setDistance(distance);
        driver.setDeliveriesToDriver(delivery);
        driverRepository.save(driver);
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {
        List<Driver> OrderDistanceDrivers = driverRepository.OrderByDistanceDesc();
        List<DriverDistance> distanceDrivers = new ArrayList<>();
        for(Driver d: OrderDistanceDrivers){
            distanceDrivers.add(new DriverDistanceImpl(d));
        }
        System.out.println("\n=== Drivers Rank Report ===");
        printReport(distanceDrivers);
        return distanceDrivers;
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
      List<Driver> driversByCity = driverRepository.findAllDriversByCityOrderByDistanceDesc(city);
        List<DriverDistance> distanceDriversByCity = new ArrayList<>();
        for(Driver d: driversByCity){
            distanceDriversByCity.add(new DriverDistanceImpl(d));
        }
        System.out.println("\n=== Drivers Rank Report by city: " +city.getName()+" ===");
        printReport(distanceDriversByCity);
        return distanceDriversByCity;
    }

    private void printReport(List<DriverDistance> driversList){
        System.out.printf("%10s %1s %10s", "Driver name", "|", "Total distance");
        System.out.println();
        System.out.println("-----------------------------");
        for(DriverDistance d: driversList){
            System.out.format("%8s %4s %8s", d.getDriver().getName(),"|" , d.getTotalDistance()+"\n");
        }
        System.out.println("-----------------------------\n");
    }

    private String toStringDeliveryInformation(Delivery delivery) {
        String info = "\n=== Delivery info ===\n" + "ID delivery: "+delivery.getId()+"\n";
        info += "Delivery Time: " + dateFormat.format(delivery.getDeliveryTime()) + "\n";
        info += "Customer: " + delivery.getCustomer().getName() + ", " + delivery.getCustomer().getCity().getName() + "\n";
        info += "Restaurant: " + delivery.getRestaurant().getName() + ", " + delivery.getRestaurant().getCity().getName() + "\n";
        info += "Driver: " + delivery.getDriver().getName() + ", " + delivery.getDriver().getCity().getName() + ", " +
                String.format("%.2f",  delivery.getDistance())  + "km \n";
        return info;
    }

    @Override
    public void setDriverRepository(DriverRepository drivers) {
        driverRepository = drivers;
    }

    public  void setDeliveryRepository(DeliveryRepository delivery){
        deliveryRepository = delivery;
    }

    private double getDistance(){
        return ThreadLocalRandom.current().nextDouble(20);
    }
}