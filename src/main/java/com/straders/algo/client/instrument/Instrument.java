package com.straders.algo.client.instrument;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

public class Instrument {

	public String getInstrument(String symbol) {
		try {
			File file = ResourceUtils.getFile("classpath:templates/instrument.txt");
			try (Stream<String> streamOfLines = Files.lines(file.toPath())) {
				Optional<String> line = streamOfLines.filter(l -> (l.contains("EQ,NSE,NSE") && l.contains(symbol)))
						.findFirst();
				if (line.isPresent()) {
					return line.get().split(",")[1];
				} else
					return StringUtils.EMPTY;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;

	}
}
