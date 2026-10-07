package nclan.ac.gameshopapp.module;

import java.util.ArrayList;
import java.util.List;

public class Address {
    private String houseNumber;
    private String street;
    private String city;
    private String postcode;
    private String country;

    public Address(String houseNumber, String street, String city, String postcode, String country) {
        this.houseNumber = clean(houseNumber);
        this.street = clean(street);
        this.city = clean(city);
        this.postcode = clean(postcode).toUpperCase();
        this.country = clean(country);
    }

    public String getHouseNumber() { return houseNumber; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getPostcode() { return postcode; }
    public String getCountry() { return country; }

    public boolean isComplete() {
        return !houseNumber.isBlank() && !street.isBlank() && !city.isBlank()
                && !postcode.isBlank() && !country.isBlank();
    }

    public String getFullAddress() {
        List<String> parts = new ArrayList<>();
        String firstLine = (houseNumber + " " + street).trim();
        if (!firstLine.isBlank()) parts.add(firstLine);
        if (!city.isBlank()) parts.add(city);
        if (!postcode.isBlank()) parts.add(postcode);
        if (!country.isBlank()) parts.add(country);
        return String.join(", ", parts);
    }

    private String clean(String value) { return value == null ? "" : value.trim(); }

    @Override
    public String toString() { return getFullAddress(); }
}
