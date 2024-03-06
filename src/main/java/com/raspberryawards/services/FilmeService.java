package com.raspberryawards.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.raspberryawards.model.FilmeModel;

public interface FilmeService {

	FilmeModel save(FilmeModel filmeModel);

	Optional<FilmeModel> findById(Long filmeId);

	List<FilmeModel> findAll();
	
	void delete(FilmeModel filmeModel);

	Map<String, List<Map<String, Object>>> obtemIntervalosPremios();

	List<FilmeModel> obtemTodosFilmesPorProdutor();

}
