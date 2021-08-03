package one.digitalinnovation.beerstock.builder;

import lombok.Builder;
import one.digitalinnovation.beerstock.dto.SodaDTO;
import one.digitalinnovation.beerstock.enums.SodaType;

@Builder
public class SodaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private SodaType type = SodaType.LAGER;

    public SodaDTO toBeerDTO() {
        return new SodaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
