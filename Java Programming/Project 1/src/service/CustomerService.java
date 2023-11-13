package service;

import model.Customer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CustomerService {
    private static final CustomerService CUSTOMER_SERVICE = new CustomerService();
    public static Map<String, Customer> customerMap = new HashMap<>();

    private CustomerService() {}
    public static CustomerService getCustomerService(){
        return CUSTOMER_SERVICE;
    }

    public void addCustomer(String email, String firstName, String lastName) {
        if (customerMap.containsKey(email))
            throw new IllegalArgumentException("Email is already exists");
        customerMap.put(email, new Customer(firstName, lastName, email));
    }
    public Customer getCustomer(String customerEmail){
        Customer customer = customerMap.get(customerEmail);
        if (customer == null) throw new IllegalArgumentException("Can not found any customer with email: " + customerEmail);
        return customer;
    }

    public Collection<Customer> getAllCustomers() {
        return customerMap.values();
    }
}
