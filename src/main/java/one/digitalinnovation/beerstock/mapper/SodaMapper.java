package one.digitalinnovation.beerstock.mapper;

import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.entity.Soda;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SodaMapper {

    SodaMapper INSTANCE = Mappers.getMapper(SodaMapper.class);

    Soda toModel(SodaDTO beerDTO);

    SodaDTO toDTO(Soda beer);
}
