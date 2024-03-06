package com.raspberryawards.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.raspberryawards.model.FilmeModel;

@Repository
public interface FilmeRepository extends JpaRepository<FilmeModel, UUID>, JpaSpecificationExecutor<FilmeModel> {
	void delete(FilmeModel filmeModel);

	Optional<FilmeModel> findById(Long id);
}
