package com.codecademy.boots.controllers;
import com.codecademy.boots.repositories.BootRepository;
import java.lang.Iterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.codecademy.boots.entities.Boot;
import com.codecademy.boots.enums.BootType;
import com.codecademy.boots.exceptions.QueryNotSupportedException;
import com.codecademy.boots.exceptions.NotImplementedException;

@RestController
@RequestMapping("/api/v1/boots")
public class BootController {
  private final BootRepository bootRepository;
  public BootController(BootRepository bootRepository) {
    this.bootRepository = bootRepository;
}
	@GetMapping("/")
public Iterable<Boot> getAllBoots() {
    return bootRepository.findAll();
}

	@GetMapping("/types")
	public List<BootType> getBootTypes() {
		return Arrays.asList(BootType.values());
	}

	@PostMapping("/")
public Boot addBoot(@RequestBody Boot boot) {
    return bootRepository.save(boot);
}

	@DeleteMapping("/{id}")
public Boot deleteBoot(@PathVariable("id") Integer id) {
    Optional<Boot> bootOptional = bootRepository.findById(id);
    if (bootOptional.isPresent()) {
        Boot boot = bootOptional.get();
        bootRepository.delete(boot);
        return boot;
    }
    return null;
}

	@PutMapping("/{id}/quantity/increment")
public Boot incrementQuantity(@PathVariable("id") Integer id) {
    Optional<Boot> optionalBoot = bootRepository.findById(id);
    if (optionalBoot.isPresent()) {
        Boot boot = optionalBoot.get();
        boot.setQuantity(boot.getQuantity() + 1);
        return bootRepository.save(boot);
    }
    return null;
}


@PutMapping("/{id}/quantity/decrement")
public Boot decrementQuantity(@PathVariable("id") Integer id) {
    Optional<Boot> optionalBoot = bootRepository.findById(id);
    if (optionalBoot.isPresent()) {
        Boot boot = optionalBoot.get();
        boot.setQuantity(boot.getQuantity() - 1);
        return bootRepository.save(boot);
    }
    return null;
}


	@GetMapping("/search")
public List<Boot> searchBoots(@RequestParam(required = false) String material,
    @RequestParam(required = false) BootType type,
    @RequestParam(required = false) Float size,
    @RequestParam(required = false, name = "quantity") Integer minQuantity) throws QueryNotSupportedException {

    if (Objects.nonNull(material)) {
        if (Objects.nonNull(type) && Objects.nonNull(size) && Objects.nonNull(minQuantity)) {
            return bootRepository.findByMaterialAndTypeAndSizeAndQuantityGreaterThan(material, type, size, minQuantity);
        } else if (Objects.nonNull(type) && Objects.nonNull(size)) {
            return bootRepository.findByMaterialAndTypeAndSize(material, type, size);
        } else if (Objects.nonNull(type) && Objects.nonNull(minQuantity)) {
            return bootRepository.findByMaterialAndTypeAndQuantityGreaterThan(material, type, minQuantity);
        } else if (Objects.nonNull(type)) {
            return bootRepository.findByMaterialAndType(material, type);
        } else {
            return bootRepository.findByMaterial(material);
        }
    } else if (Objects.nonNull(type)) {
        if (Objects.nonNull(size) && Objects.nonNull(minQuantity)) {
            return bootRepository.findByTypeAndSizeAndQuantityGreaterThan(type, size, minQuantity);
        } else if (Objects.nonNull(size)) {
            return bootRepository.findByTypeAndSize(type, size);
        } else if (Objects.nonNull(minQuantity)) {
            return bootRepository.findByTypeAndQuantityGreaterThan(type, minQuantity);
        } else {
            return bootRepository.findByType(type);
        }
    } else if (Objects.nonNull(size)) {
        if (Objects.nonNull(minQuantity)) {
            return bootRepository.findBySizeAndQuantityGreaterThan(size, minQuantity);
        } else {
            return bootRepository.findBySize(size);
        }
    } else if (Objects.nonNull(minQuantity)) {
        return bootRepository.findByQuantityGreaterThan(minQuantity);
    } else {
        throw new QueryNotSupportedException("This query is not supported! Try a different combination of search parameters.");
    }
}


}