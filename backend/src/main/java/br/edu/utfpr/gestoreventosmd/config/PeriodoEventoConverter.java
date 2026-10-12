package br.edu.utfpr.gestoreventosmd.config;

import br.edu.utfpr.gestoreventosmd.dto.PeriodoEvento;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PeriodoEventoConverter implements Converter<String, PeriodoEvento> {

    @Override
    public PeriodoEvento convert(String source) {
        return PeriodoEvento.valueOf(source.trim().toUpperCase(Locale.ROOT));
    }
}
