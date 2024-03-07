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

	public Map<String, List<Map<String, Object>>> obtemIntervalosPremios() {
		List<FilmeModel> filmesPorProdutor = obtemTodosFilmesPorProdutor();

		// Agrupar filmes por produtor
		Map<String, List<FilmeModel>> filmesAgrupados = agruparFilmesPorProdutor(filmesPorProdutor);

		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		result.put("min", obtemProdutorComMenorIntervaloProducao(filmesAgrupados));
		result.put("max", obtemProdutorComMaiorIntervaloProducao(filmesAgrupados));

		return result;
	}

	public List<FilmeModel> obtemTodosFilmesPorProdutor() {
		// Obtem os filmes vencedores
		List<FilmeModel> todosFilmes = getAll().stream().filter(filme -> Boolean.TRUE.equals(filme.getVencedor()))
				.collect(Collectors.toList());

		// Expande os filmes cencedores para cada produtor que estão separados por ','
		// ou 'and'
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

	private List<Map<String, Object>> obtemProdutorComMaiorIntervaloProducao(Map<String, List<FilmeModel>> filmes) {
		List<Map<String, Object>> result = new ArrayList<>();

		Integer maiorIntervalo = null;
		Map<String, Object> maiorIntervalolMap = null;

		for (String produtor : filmes.keySet()) {

			// Pega a lista de filmes do produtor
			List<FilmeModel> filmesDoProdutor = filmes.get(produtor).stream().collect(Collectors.toList());

			// Percorre os filmes pra encontrar o maior intervalo.
			for (int i = 0; i < filmesDoProdutor.size() - 1; i++) {

				int intervalo = filmesDoProdutor.get(i + 1).getAnoLancamento()
						- filmesDoProdutor.get(i).getAnoLancamento();

				if (intervalo == 0)
					continue;

				if (null == maiorIntervalo || intervalo >= maiorIntervalo) {
					maiorIntervalo = intervalo;
					maiorIntervalolMap = obtemMapaIntervaloProdutor(produtor, intervalo, filmesDoProdutor.get(i),
							filmesDoProdutor.get(i + 1));
				}
			}

		}

		// Adiciona o mapa de intervalo do produtor no resultado
		if (maiorIntervalolMap != null) {
			result.add(maiorIntervalolMap);
		}

		return result;
	}

	private List<Map<String, Object>> obtemProdutorComMenorIntervaloProducao(Map<String, List<FilmeModel>> filmes) {
		List<Map<String, Object>> result = new ArrayList<>();

		Integer menorIntervalo = null;
		Map<String, Object> menorIntervalolMap = null;

		for (String produtor : filmes.keySet()) {

			// Pega a lista de filmes do produtor
			List<FilmeModel> filmesDoProdutor = filmes.get(produtor).stream().collect(Collectors.toList());

			// Percorre os filmes pra encontrar o maior intervalo.
			for (int i = 0; i < filmesDoProdutor.size() - 1; i++) {

				int intervalo = filmesDoProdutor.get(i + 1).getAnoLancamento()
						- filmesDoProdutor.get(i).getAnoLancamento();

				if (intervalo == 0)
					continue;

				if (null == menorIntervalo || intervalo < menorIntervalo) {
					menorIntervalo = intervalo;
					menorIntervalolMap = obtemMapaIntervaloProdutor(produtor, intervalo, filmesDoProdutor.get(i),
							filmesDoProdutor.get(i + 1));
				}
			}
		}

		// Adiciona o mapa de intervalo do produtor no resultado
		if (menorIntervalolMap != null) {
			result.add(menorIntervalolMap);
		}

		return result;
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
