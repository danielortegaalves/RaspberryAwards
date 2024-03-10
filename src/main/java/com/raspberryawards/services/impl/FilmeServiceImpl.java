package com.raspberryawards.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.raspberryawards.model.FilmeModel;
import com.raspberryawards.repository.FilmeRepository;
import com.raspberryawards.services.FilmeService;

import jakarta.transaction.Transactional;

@Service
public class FilmeServiceImpl implements FilmeService {
	@Autowired
	private FilmeRepository filmeRepository;

	public List<FilmeModel> obtemTodosFilmesPorProdutor() {
		List<FilmeModel> todosFilmes = getAll().stream().filter(filme -> Boolean.TRUE.equals(filme.getVencedor()))
				.collect(Collectors.toList());

		// Cria os filmes para cada produtor que estão separados por ',' ou 'and'
		return todosFilmes.stream().flatMap(filme -> expandirFilmesPorProdutor(filme).stream())
				.collect(Collectors.toList());

	}

	private List<FilmeModel> expandirFilmesPorProdutor(FilmeModel filme) {
		String nomeProdutor = filme.getProdutor();

		// Divide a string de produtores com base em ',' ou 'and'
		List<String> produtores = Arrays.asList(nomeProdutor.split(",|and"));

		// Cria novos filmes para cada produtor
		return produtores.stream().filter(produtor -> !produtor.trim().isEmpty()).map(producer -> {
			FilmeModel novofilme = new FilmeModel();
			novofilme.setAnoLancamento(filme.getAnoLancamento());
			novofilme.setTitulo(filme.getTitulo());
			novofilme.setEstudio(filme.getEstudio());
			novofilme.setProdutor(producer.trim());
			novofilme.setVencedor(filme.getVencedor());
			return novofilme;
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> obtemProdutorComMaiorIntervaloProducao(Map<String, List<FilmeModel>> filmes) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (String produtor : filmes.keySet()) {

			// Pega a lista de filmes do produtor, e ordena pelo ano de lançamento.
			var filmesDoProdutor = filmes.get(produtor).stream()
					.sorted(Comparator.comparingInt(FilmeModel::getAnoLancamento)).collect(Collectors.toList());

			adicionaProdutoresComIntervaloCalculado(result, produtor, filmesDoProdutor);

		}

		// Encontra o maior intervalo na lista
		int maiorIntervalo = result.stream().mapToInt(entry -> (int) entry.get("interval")).max().orElse(0);

		// Filtra os objetos que têm o maior intervalo
		var produtorMaiorIntervalo = result.stream()
				.filter(entry -> (int) entry.get("interval") == maiorIntervalo).collect(Collectors.toList());

		produtorMaiorIntervalo
				.sort(Comparator.comparingInt(map -> (int) ((Map<String, Object>) map).get("interval")).reversed());
		
		return produtorMaiorIntervalo;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> obtemProdutorComMenorIntervaloProducao(Map<String, List<FilmeModel>> filmes) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (String produtor : filmes.keySet()) {

			// Pega a lista de filmes do produtor, e ordena pelo ano de lançamento.
			var filmesDoProdutor = filmes.get(produtor).stream()
					.sorted(Comparator.comparingInt(FilmeModel::getAnoLancamento)).collect(Collectors.toList());

			// Adiciona os produtores com intervalo calculado
			adicionaProdutoresComIntervaloCalculado(result, produtor, filmesDoProdutor);

		}

		// Encontra o menor intervalo na lista
		int menorIntervalo = result.stream().mapToInt(entry -> (int) entry.get("interval")).min().orElse(0);

		// Filtra os objetos que têm o menor intervalo
		var produtorMenorIntervalo = result.stream()
				.filter(entry -> (int) entry.get("interval") == menorIntervalo).collect(Collectors.toList());

		produtorMenorIntervalo
				.sort(Comparator.comparingInt(map -> (int) ((Map<String, Object>) map).get("interval")).reversed());
		
		return produtorMenorIntervalo;
	}

	private void adicionaProdutoresComIntervaloCalculado(List<Map<String, Object>> result, String produtor,
			List<FilmeModel> filmesDoProdutor) {
		
		for (int i = 0; i < filmesDoProdutor.size() - 1; i++) {

			int intervalo = filmesDoProdutor.get(i + 1).getAnoLancamento()
					- filmesDoProdutor.get(i).getAnoLancamento();

			if (intervalo == 0)
				continue;

			var produtorIntervalo = obtemMapaIntervaloProdutor(produtor, intervalo, filmesDoProdutor.get(i),
					filmesDoProdutor.get(i + 1));
			
			result.add(produtorIntervalo);
		}
	}

	private Map<String, Object> obtemMapaIntervaloProdutor(String produtor, int intervalo, FilmeModel primeiroPremio,
			FilmeModel premioSeguinte) {
		Map<String, Object> intervalMap = new HashMap<>();
		intervalMap.put("producer", produtor);
		intervalMap.put("interval", intervalo);
		intervalMap.put("previousWin", primeiroPremio.getAnoLancamento());
		intervalMap.put("followingWin", premioSeguinte.getAnoLancamento());
		return intervalMap;
	}

	public Map<String, List<Map<String, Object>>> obtemIntervalosPremios() {
		List<FilmeModel> filmesPorProdutor = obtemTodosFilmesPorProdutor();

		// Agrupar filmes por produtor
		Map<String, List<FilmeModel>> filmesAgrupados = agruparFilmesPorProdutor(filmesPorProdutor);

		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		result.put("min", obtemProdutorComMenorIntervaloProducao(filmesAgrupados));
		result.put("max", obtemProdutorComMaiorIntervaloProducao(filmesAgrupados));

		return result;
	}

	// O uso do TreeMap para ser ordenado pelas chaves (nomes dos produtores)
	private Map<String, List<FilmeModel>> agruparFilmesPorProdutor(List<FilmeModel> filmes) {
		return filmes.stream().collect(Collectors.groupingBy(FilmeModel::getProdutor,
				TreeMap<String, List<FilmeModel>>::new, Collectors.toList()));
	}

	@Override
	public FilmeModel save(FilmeModel filmeModel) {
		return filmeRepository.save(filmeModel);
	}

	@Override
	public Optional<FilmeModel> findById(Long filmeId) {
		return filmeRepository.findById(filmeId);
	}

	@Override
	public List<FilmeModel> findAll() {
		return filmeRepository.findAll();
	}

	@Transactional
	@Override
	public void delete(FilmeModel filmeModel) {
		filmeRepository.delete(filmeModel);
	}

	public List<FilmeModel> getAll() {
		return filmeRepository.findAll();
	}

}
