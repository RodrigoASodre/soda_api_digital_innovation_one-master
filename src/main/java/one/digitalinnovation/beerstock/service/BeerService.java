package one.digitalinnovation.beerstock.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.entity.Soda;
import one.digitalinnovation.beerstock.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.SodaNotFoundException;
import one.digitalinnovation.beerstock.exception.SodaStockExceededException;
import one.digitalinnovation.beerstock.mapper.SodaMapper;
import one.digitalinnovation.beerstock.repository.SodaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final SodaRepository beerRepository;
    private final SodaMapper beerMapper = SodaMapper.INSTANCE;

    public SodaDTO createBeer(SodaDTO beerDTO) throws SodaAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Soda beer = beerMapper.toModel(beerDTO);
        Soda savedBeer = beerRepository.save(beer);
        return beerMapper.toDTO(savedBeer);
    }

    public SodaDTO findByName(String name) throws SodaNotFoundException {
        Soda foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new SodaNotFoundException(name));
        return beerMapper.toDTO(foundBeer);
    }

    public List<SodaDTO> listAll() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws SodaNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws SodaAlreadyRegisteredException {
        Optional<Soda> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new SodaAlreadyRegisteredException(name);
        }
    }

    private Soda verifyIfExists(Long id) throws SodaNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new SodaNotFoundException(id));
    }

    public SodaDTO increment(Long id, int quantityToIncrement) throws SodaNotFoundException, SodaStockExceededException {
        Soda beerToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
            Soda incrementedBeerStock = beerRepository.save(beerToIncrementStock);
            return beerMapper.toDTO(incrementedBeerStock);
        }
        throw new SodaStockExceededException(id, quantityToIncrement);
    }
}
