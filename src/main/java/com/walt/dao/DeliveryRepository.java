package com.walt.dao;

import com.walt.model.City;
import com.walt.model.Customer;
import com.walt.model.Delivery;
import com.walt.model.Driver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {

    List<Delivery> findAllDeliveryByCustomer(Customer customer);

    List<Delivery> findAllDeliveryByDriver(Driver driver);

}


