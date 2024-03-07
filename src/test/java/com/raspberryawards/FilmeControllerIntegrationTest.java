package com.raspberryawards;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmeControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testObtemIntervalosDePremiosInseridosNoBancoDeDados() {
		// Testa o endpoint "/intervalos-premio" e obtém a resposta
		Map<String, Object> response = restTemplate
				.getForObject("http://localhost:" + port + "/filmes/intervalos-premio", Map.class);

		// Verifica se a resposta não é nula
		assertNotNull(response);

		// Verifica se a resposta contém as chaves esperadas
		assertTrue(response.containsKey("min"));
		assertTrue(response.containsKey("max"));

		// Verifica se as listas não estão vazias
		List<Map<String, Object>> minList = (List<Map<String, Object>>) response.get("min");
		List<Map<String, Object>> maxList = (List<Map<String, Object>>) response.get("max");
		assertFalse(minList.isEmpty());
		assertFalse(maxList.isEmpty());

		// Verifica se minList contém o produtor "Joel Silver" com intervalo 1
		Optional<Map<String, Object>> produtorMenorIntervalo = minList.stream()
				.filter(entry -> "Joel Silver".equals(entry.get("producer")))
				.filter(entry -> 1 == (int) entry.get("interval"))
				.filter(entry -> 1990 == (int) entry.get("previousWin"))
				.filter(entry -> 1991 == (int) entry.get("followingWin")).findFirst();
		assertTrue(produtorMenorIntervalo.isPresent());

		// Verifica se maxList contém o produtor "Matthew Vaughn" com intervalo 13
		Optional<Map<String, Object>> produtorMaiorIntervalo = maxList.stream()
				.filter(entry -> "Matthew Vaughn".equals(entry.get("producer")))
				.filter(entry -> 13 == (int) entry.get("interval"))
				.filter(entry -> 2002 == (int) entry.get("previousWin"))
				.filter(entry -> 2015 == (int) entry.get("followingWin")).findFirst();
		assertTrue(produtorMaiorIntervalo.isPresent());
	}

}