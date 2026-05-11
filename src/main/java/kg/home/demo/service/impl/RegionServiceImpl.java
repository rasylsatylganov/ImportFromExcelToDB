package kg.home.demo.service.impl;

import kg.home.demo.entity.Region;
import kg.home.demo.repository.RegionRepository;
import kg.home.demo.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Optional<Region> getRegionById(Integer id) {
        return regionRepository.findById(id);
    }

    @Override
    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    @Override
    public Region updateRegion(Integer id, Region region) {
        return regionRepository.findById(id)
                .map(existing -> {
                    existing.setCodeRegion(region.getCodeRegion());
                    existing.setNameRegion(region.getNameRegion());
                    existing.setOrderNumber(region.getOrderNumber());
                    return regionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));
    }

    @Override
    public void deleteRegion(Integer id) {
        regionRepository.deleteById(id);
    }
}

