package one.digitalinnovation.beerstock.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.dto.QuantityDTO;
import one.digitalinnovation.beerstock.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.SodaNotFoundException;
import one.digitalinnovation.beerstock.exception.SodaStockExceededException;
import one.digitalinnovation.beerstock.service.BeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SodaController implements SodaControllerDocs {

    private final BeerService beerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SodaDTO createBeer(@RequestBody @Valid SodaDTO beerDTO) throws SodaAlreadyRegisteredException {
        return beerService.createBeer(beerDTO);
    }

    @GetMapping("/{name}")
    public SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException {
        return beerService.findByName(name);
    }

    @GetMapping
    public List<SodaDTO> listBeers() {
        return beerService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws SodaNotFoundException {
        beerService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public SodaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws SodaNotFoundException, SodaStockExceededException {
        return beerService.increment(id, quantityDTO.getQuantity());
    }
}
