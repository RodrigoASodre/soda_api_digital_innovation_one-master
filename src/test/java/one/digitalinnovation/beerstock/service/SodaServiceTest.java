package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.SodaDTOBuilder;
import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.entity.Soda;
import one.digitalinnovation.beerstock.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.SodaNotFoundException;
import one.digitalinnovation.beerstock.exception.SodaStockExceededException;
import one.digitalinnovation.beerstock.mapper.SodaMapper;
import one.digitalinnovation.beerstock.repository.SodaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SodaServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private SodaRepository sodaRepository;

    private SodaMapper sodaMapper = SodaMapper.INSTANCE;

    @InjectMocks
    private SodaService sodaService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws SodaAlreadyRegisteredException {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toBeerDTO();
        Soda expectedSavedSoda = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.empty());
        when(SodaRepository.save(expectedSavedSoda)).thenReturn(expectedSavedSoda);

        //then
        SodaDTO createdSodaDTO = sodaService.createSoda(expectedSodaDTO);

        assertThat(createdSodaDTO.getId(), is(equalTo(expectedSodaDTO.getId())));
        assertThat(createdSodaDTO.getName(), is(equalTo(expectedSodaDTO.getName())));
        assertThat(createdSodaDTO.getQuantity(), is(equalTo(expectedSodaDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredSodaInformedThenAnExceptionShouldBeThrown() {
        // given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda duplicatedBeer = sodaMapper.toModel(expectedSodaDTO);

        // when
        when(SodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        // then
        assertThrows(SodaAlreadyRegisteredException.class, () -> sodaService.createBeer(expectedBeerDTO));
    }

    @Test
    void whenValidSodaNameIsGivenThenReturnASoda() throws SodaNotFoundException {
        // given
        SodaDTO expectedFoundBeerDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        // when
        when(SodaRepository.findByName(expectedFoundSoda.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // then
        SodaDTO foundSodaDTO = sodaService.findByName(expectedFoundSodaDTO.getName());

        assertThat(foundSodaDTO, is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // given
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        // when
        when(SodaRepository.findByName(expectedFoundSodaDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(SodaNotFoundException.class, () -> sodaService.findByName(expectedFoundSodaDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfSodas() {
        // given
        SodaDTO expectedFoundBeerDTO = SodaDTOBuilder.builder().build().toBeerDTO();
        Soda expectedFoundBeer = sodaMapper.toModel(expectedFoundSodaDTO);

        //when
        when(sodaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        //then
        List<SodaDTO> foundListSodasDTO = sodaService.listAll();

        assertThat(foundListSodasDTO, is(not(empty())));
        assertThat(foundListSodasDTO.get(0), is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        //when
        when(sodaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<SodaDTO> foundListBeersDTO = sodaService.listAll();

        assertThat(foundListBeersDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws SodaNotFoundException {
        // given
        SodaDTO expectedDeletedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedDeletedBeer = sodaMapper.toModel(expectedDeletedSodaDTO);

        // when
        when(sodaRepository.findById(expectedDeletedSodaDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(sodaRepository).deleteById(expectedDeletedSodaDTO.getId());

        // then
        sodaService.deleteById(expectedDeletedSodaDTO.getId());

        verify(sodaRepository, times(1)).findById(expectedDeletedSodaDTO.getId());
        verify(sodaRepository, times(1)).deleteById(expectedDeletedSodaDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementSodaStock() throws SodaNotFoundException, SodaStockExceededException {
        //given
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        //when
        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(sodaRepository.save(expectedSoda)).thenReturn(expectedSoda);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedSodaDTO.getQuantity() + quantityToIncrement;

        // then
        SodaDTO incrementedSodaDTO = sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedSodaDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedSodaDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        SodaDTO expectedBeerDTO = SodaDTOBuilder.builder().build().toBeerDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 80;
        assertThrows(SodaStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        SodaDTO expectedBeerDTO = SodaDTOBuilder.builder().build().toBeerDTO();
        Soda expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 45;
        assertThrows(SodaStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }
//
//    @Test
//    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//
//        int quantityToDecrement = 80;
//        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
//    }
}
