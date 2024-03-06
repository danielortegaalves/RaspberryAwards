package com.raspberryawards.loaders;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.raspberryawards.model.FilmeModel;
import com.raspberryawards.repository.FilmeRepository;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final char CSV_DELIMITER = ';';
	private static final String STRING_YES = "yes";
	@Autowired
    private FilmeRepository filmeRepository;

    @Override
    public void run(String... args) throws Exception {
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/movielist.csv"))) {
            CSVReader csvReader = new CSVReaderBuilder(reader)
            		.withCSVParser(new CSVParserBuilder().withSeparator(CSV_DELIMITER).build())
            		.withSkipLines(1).build();
            List<String[]> rows = csvReader.readAll();

            for (String[] row : rows) {
            	var filme = FilmeModel.builder()
            	.anoLancamento(Integer.valueOf(row[0]))
            	.titulo(row[1])
            	.estudio(row[2])
            	.produtor(row[3])
            	.vencedor(STRING_YES.equalsIgnoreCase(row[4])).build();
            	
            	filmeRepository.save(filme);
            }
        }
    }
}
