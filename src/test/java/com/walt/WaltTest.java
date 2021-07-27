package com.walt;

import com.walt.dao.*;
import com.walt.exception.DriverLessException;
import com.walt.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.Assert.*;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.assertEquals;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WaltTest {

    @TestConfiguration
    static class WaltServiceImplTestContextConfiguration {

        @Bean
        public WaltService waltService() {
            return new WaltServiceImpl();
        }
    }

    @Autowired
    WaltService waltService;

    @Resource
    CityRepository cityRepository;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    @BeforeEach()
    public void prepareData() throws DriverLessException {
        waltService = new WaltServiceImpl();  // Create new waltService

        City jerusalem = new City("Jerusalem");
        City tlv = new City("Tel-Aviv");
        City bash = new City("Beer-Sheva");
        City haifa = new City("Haifa");

        cityRepository.saveAll(Lists.newArrayList(jerusalem,tlv,bash,haifa));

        createDrivers(jerusalem, tlv, bash, haifa);
        waltService.setDriverRepository(driverRepository); // Assign DriverRepository
        createCustomers(jerusalem, tlv, haifa);

        createRestaurant(jerusalem, tlv);

        // Create same deliveries
        waltService.setDeliveryRepository(deliveryRepository);
        createDeliveries();

    }

    private void createRestaurant(City jerusalem, City tlv) {
        Restaurant meat1 = new Restaurant("meat1", jerusalem, "All meat restaurant");
        Restaurant meat2 = new Restaurant("meat2", jerusalem, "All meat restaurant");
        Restaurant meat3 = new Restaurant("meat3", jerusalem, "All meat restaurant");
        Restaurant meat4 = new Restaurant("meat4", jerusalem, "All meat restaurant");
        Restaurant vegan = new Restaurant("vegan", tlv, "Only vegan");
        Restaurant cafe = new Restaurant("cafe", tlv, "Coffee shop");
        Restaurant chinese = new Restaurant("chinese", tlv, "chinese restaurant");
        Restaurant mexican = new Restaurant("restaurant", tlv, "mexican restaurant ");

        restaurantRepository.saveAll(Lists.newArrayList(meat1, meat2, meat3, meat4, vegan, cafe, chinese, mexican));
    }

    private void createCustomers(City jerusalem, City tlv, City haifa) {
        Customer beethoven = new Customer("Beethoven", tlv, "Ludwig van Beethoven");
        Customer mozart = new Customer("Mozart", jerusalem, "Wolfgang Amadeus Mozart");
        Customer chopin = new Customer("Chopin", haifa, "Frédéric François Chopin");
        Customer rachmaninoff = new Customer("Rachmaninoff", tlv, "Sergei Rachmaninoff");
        Customer bach = new Customer("Bach", tlv, "Sebastian Bach. Johann");

        customerRepository.saveAll(Lists.newArrayList(beethoven, mozart, chopin, rachmaninoff, bach));
    }

    private void createDrivers(City jerusalem, City tlv, City bash, City haifa) {
        Driver mary = new Driver("Mary", tlv);
        Driver patricia = new Driver("Patricia", tlv);
        Driver jennifer = new Driver("Jennifer", haifa);
        Driver james = new Driver("James", bash);
        Driver john = new Driver("John", bash);
        Driver robert = new Driver("Robert", jerusalem);
        Driver david = new Driver("David", jerusalem);
        Driver daniel = new Driver("Daniel", tlv);
        Driver noa = new Driver("Noa", haifa);
        Driver ofri = new Driver("Ofri", haifa);
        Driver nata = new Driver("Neta", jerusalem);

        driverRepository.saveAll(Lists.newArrayList(mary, patricia, jennifer, james, john, robert, david, daniel, noa, ofri, nata));
    }

    private void createDeliveries() throws DriverLessException {

        Delivery d1 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Beethoven"),
                restaurantRepository.findByName("vegan"), getDate(2021, 7, 16, 8, 0, 0));
        Delivery d2 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Rachmaninoff"),
                restaurantRepository.findByName("restaurant"), getDate(2021, 7,16, 7, 55, 45));
        //An Exception should be printed.
        Delivery d3 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("cafe"), getDate(2021 , 7, 24, 14, 32, 6));
        Delivery d4 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Bach"),
                restaurantRepository.findByName("restaurant"), getDate(2021, 7,23, 9, 17, 55));
        Delivery d5 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("meat1"), getDate(2021, 7,23, 13, 3, 0));
        Delivery d6 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("meat2"), getDate(2021, 7,23, 13, 31, 0));
        Delivery d7 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("meat3"), getDate(2021, 7,23, 13, 55, 0));
        //An Exception should be printed.
        Delivery d8 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("meat4"), getDate(2021, 7,23, 14, 0, 0));

        Delivery d9 = waltService.createOrderAndAssignDriver(customerRepository.findByName("Mozart"),
                restaurantRepository.findByName("meat4"), getDate(2021, 7,23, 14, 5, 0));

        waltService.getDriverRankReport();
        waltService.getDriverRankReportByCity(cityRepository.findByName("Jerusalem"));

        // For avoiding error in delivery
       ArrayList<Delivery> deliveries = Lists.newArrayList(d1, d2, d3, d4, d5, d6, d7, d8, d9);
        for (Delivery d: deliveries){
            if(d != null){
                deliveryRepository.save(d);
            }
        }
    }

    private Date getDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second){
        return new GregorianCalendar(year, month,dayOfMonth, hourOfDay, minute, second).getTime();
    }


    @Test
    public void testBasics(){

        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("Beer-Sheva")).size()), 2);

        assertEquals((customerRepository.findAllCustomerByCity(cityRepository.findByName("Tel-Aviv"))).size(), 3);

        assertEquals((restaurantRepository.findAllRestaurantByCity(cityRepository.findByName("Jerusalem"))).size(), 4);

        assertEquals((deliveryRepository.findAllDeliveryByDriver(driverRepository.findByName("Robert"))).size(),
                (driverRepository.findByName("Robert").getDeliveriesNumber()));

        assertTrue(((deliveryRepository.findAllDeliveryByCustomer(customerRepository.findByName("Bach")).size() == 1) &&
                (deliveryRepository.findAllDeliveryByCustomer(customerRepository.findByName("Bach")).get(0).getId() == 31)));

        assertTrue(deliveryRepository.findAllDeliveryByDriver(driverRepository.findByName("Batel")).isEmpty());

        assertNotNull("The deliveryRepository not null", deliveryRepository);

        assertNotSame(deliveryRepository, customerRepository);

        //False
         assertEquals((restaurantRepository.findAllRestaurantByCity(cityRepository.findByName("Haifa"))).size(), 4);
         assertTrue((deliveryRepository.findAllDeliveryByCustomer(customerRepository.findByName("Mozart")).size() == 1));

    }


}
