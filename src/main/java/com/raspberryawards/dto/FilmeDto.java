package com.raspberryawards.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FilmeDto{
	
	@NonNull
	private Integer anoLancamento;
	
	@NonNull
    private String titulo;
	
	@NonNull
    private String estudio;
	
	@NonNull
    private String produtor;
	
	@NonNull
	private Boolean vencedor;

}
