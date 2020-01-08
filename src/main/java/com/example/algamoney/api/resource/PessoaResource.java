package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	//PUBLICADOR DO EventoCriado
	//Publicador de Eventos de Aplicação - RecursoCriadoEvent é um Evento de Aplicação(ApplicationEvent)
	@Autowired
	private ApplicationEventPublisher publisher; 
	
	@GetMapping
	public List<Pessoa> listar(){
		return pessoaRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = pessoaRepository.save(pessoa);
		
	//ANTES DO EVENTO SER CRIADO
	/* 
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
						.buildAndExpand(pessoaSalva.getCodigo()).toUri();
		response.setHeader("Location", uri.toASCIIString());
	*/
		//EVENTO
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscaById(@PathVariable Long codigo) {
		Pessoa pessoa = pessoaRepository.findOne(codigo);
		return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT) //Codigo 204 Sucesso na exlusão, não tem nada para retornar
	public void remover (@PathVariable Long codigo) {
		pessoaRepository.delete(codigo);
	}
	
	//Recebe pessoa do cliente com os dados pra atualizar e copia para pessoaSalva, que tá no banco
	//tirando o código que já está setado, ele vem como null pq não é passado na atualização, mas na url
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa){
		/* Essa parte não deveria estar aqui por ser uma regra de negócio,
		 * foi criada a classe PessoaService e essa regra foi feita lá,
		 * uma classe mais específica para regras de negócio
		
		Pessoa pessoaSalva = pessoaRepository.findOne(codigo);
		
		if (pessoaSalva == null) {
			throw new EmptyResultDataAccessException(1);
		}
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		pessoaRepository.save(pessoaSalva);
		
		*/
		Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
		
	}
	
	//Atualização Parcial
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtiva(@PathVariable Long codigo, @Valid @RequestBody Boolean ativo) {
		pessoaService.atualizarPropriedadeAtiva(codigo, ativo);
	}
	
	
}
