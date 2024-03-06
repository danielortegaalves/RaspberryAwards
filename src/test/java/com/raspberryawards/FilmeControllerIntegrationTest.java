package com.raspberryawards;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.raspberryawards.dto.FilmeDto;

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
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIncluiProdutorComMaiorAndMenorIntervaloDePremios() {
		// Inserção dos filmes do produtor com menor intervalo
		FilmeDto primeiroFilmeDto = criarFilmeDto("Daniel Ortega Alves", 2023, "Inicio de tudo", "Home", true);
		FilmeDto segundoFilmeDto = criarFilmeDto("Daniel Ortega Alves", 2024, "O desafio", "Home", true);
		restTemplate.postForEntity("http://localhost:" + port + "/filmes", primeiroFilmeDto, FilmeDto.class);
		restTemplate.postForEntity("http://localhost:" + port + "/filmes", segundoFilmeDto, FilmeDto.class);

		// Inserção dos filmes do produtor com intervalo maior
		primeiroFilmeDto = criarFilmeDto("João Gonçalves", 2000, "O maior intervalo", "Home", true);
		segundoFilmeDto = criarFilmeDto("João Gonçalves", 2024, "Desafio demorado", "Home", true);
		restTemplate.postForEntity("http://localhost:" + port + "/filmes", primeiroFilmeDto, FilmeDto.class);
		restTemplate.postForEntity("http://localhost:" + port + "/filmes", segundoFilmeDto, FilmeDto.class);

		// Chama o endpoint "/intervalos-premio" para obter o resultado
		Map<String, List<Map<String, Object>>> resultado = restTemplate
				.getForObject("http://localhost:" + port + "/filmes/intervalos-premio", Map.class);

		// Verifica se existem entradas nas listas "min" e "max"
		assertTrue(resultado.get("min").size() >= 1);
		assertTrue(resultado.get("max").size() >= 1);

		//Verifica se "Daniel Ortega Alves" está entre os produtores com menor intervalo
		List<Map<String, Object>> listaMin = resultado.get("min");
		boolean produtorEncontradoMin = listaMin.stream()
				.anyMatch(entry -> "Daniel Ortega Alves".equals(entry.get("producer")));
		assertTrue(produtorEncontradoMin);

		// Verifica se "João Gonçalves" está entre os produtores com maior intervalo
		List<Map<String, Object>> listaMax = resultado.get("max");
		boolean produtorEncontradoMax = listaMax.stream()
				.anyMatch(entry -> "João Gonçalves".equals(entry.get("producer")));
		assertTrue(produtorEncontradoMax);
	}

	private FilmeDto criarFilmeDto(String produtor, int anoLancamento, String titulo, String estudio,
			boolean vencedor) {
		return FilmeDto.builder().anoLancamento(anoLancamento).titulo(titulo).estudio(estudio).vencedor(vencedor)
				.produtor(produtor).build();
	}
}