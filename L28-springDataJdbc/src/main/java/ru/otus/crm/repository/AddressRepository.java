package ru.otus.crm.repository;

import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
}
