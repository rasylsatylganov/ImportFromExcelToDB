package kg.home.demo.service.impl;


import kg.home.demo.entity.Country;
import kg.home.demo.repository.CountryRepository;
import kg.home.demo.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> getCountryById(Integer id) {
        return countryRepository.findById(id);
    }

    @Override
    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country updateCountry(Integer id, Country country) {
        return countryRepository.findById(id)
                .map(existing -> {
                    existing.setCodeCountry(country.getCodeCountry());
                    existing.setNameCountry(country.getNameCountry());
                    existing.setOrderNumber(country.getOrderNumber());
                    existing.setRegion(country.getRegion());
                    return countryRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Country not found with id " + id));
    }

    @Override
    public void deleteCountry(Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new RuntimeException("Country not found with id " + id);
        }
        countryRepository.deleteById(id);
    }
}
