package com.raspberryawards.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raspberryawards.dto.FilmeDto;
import com.raspberryawards.model.FilmeModel;
import com.raspberryawards.services.FilmeService;

@RestController
@RequestMapping("/filmes")
@Validated
public class FilmeController {

	@Autowired
	FilmeService filmeService;

	@GetMapping("/intervalos-premio")
	public Map<String, List<Map<String, Object>>> obtemIntervalosPremios() {
		return filmeService.obtemIntervalosPremios();
	}

	@PostMapping
	public ResponseEntity<Object> saveFilme(@RequestBody FilmeDto filmeDto) {
		var filmeModel = new FilmeModel();
		BeanUtils.copyProperties(filmeDto, filmeModel);
		filmeService.save(filmeModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(filmeModel);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteFilme(@PathVariable(value = "id") Long id) {
		Optional<FilmeModel> filmeModelOptional = filmeService.findById(id);
		if (!filmeModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filme não encontrado.");
		}
		filmeService.delete(filmeModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Filme deletado.");
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateFilme(@PathVariable(value = "id") Long id, @RequestBody FilmeDto filmeDto) {
		Optional<FilmeModel> filmeModelOptional = filmeService.findById(id);
		if (!filmeModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filme não encontrado.");
		}
		FilmeModel filmeModel = filmeModelOptional.get();
		filmeModel.setAnoLancamento(filmeDto.getAnoLancamento());
		filmeModel.setTitulo(filmeDto.getTitulo());
		filmeModel.setEstudio(filmeDto.getEstudio());
		filmeModel.setProdutor(filmeDto.getProdutor());
		filmeModel.setVencedor(filmeDto.getVencedor());

		filmeService.save(filmeModel);
		return ResponseEntity.status(HttpStatus.OK).body(filmeModel);
	}

	@GetMapping
	public ResponseEntity<List<FilmeModel>> getAllFilmes() {
		return ResponseEntity.status(HttpStatus.OK).body(filmeService.obtemTodosFilmesPorProdutor());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getFilme(@PathVariable(value = "id") Long id) {
		Optional<FilmeModel> filmeModelOptional = filmeService.findById(id);
		if (!filmeModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filme não encontrado.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(filmeModelOptional.get());
	}

}
