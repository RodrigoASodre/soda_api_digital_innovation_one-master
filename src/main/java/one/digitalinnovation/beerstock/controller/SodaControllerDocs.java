package one.digitalinnovation.beerstock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.SodaNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages beer stock")
public interface SodaControllerDocs {

    @ApiOperation(value = "Beer creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success beer creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    SodaDTO createBeer(SodaDTO beerDTO) throws SodaAlreadyRegisteredException;

    @ApiOperation(value = "Returns beer found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success beer found in the system"),
            @ApiResponse(code = 404, message = "Beer with given name not found.")
    })
    SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException;

    @ApiOperation(value = "Returns a list of all beers registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all beers registered in the system"),
    })
    List<SodaDTO> listBeers();

    @ApiOperation(value = "Delete a beer found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success beer deleted in the system"),
            @ApiResponse(code = 404, message = "Beer with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws SodaNotFoundException;
}
