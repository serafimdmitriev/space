package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/*
Interface CrudRepository <T,ID>
Interface for generic CRUD operations on a repository for a specific type.
The first parameter should have type of our object in database, and the
second parameter - id, unique identification number.

ShipRepository inherit all the methods we need from CrudRepository<Ship, Long>
so we don't need to create ours. Also because of that we don't need to create
our implementation of ShipRepository, Spring will do it instead of us.
 */
@Repository
public interface ShipRepository extends CrudRepository<Ship, Long> {

}
