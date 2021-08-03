package one.digitalinnovation.beerstock.repository;

import one.digitalinnovation.beerstock.entity.Soda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SodaRepository extends JpaRepository<Soda, Long> {

    Optional<Soda> findByName(String name);
}
