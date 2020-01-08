/* CLASSE DE SEGURANÇA BÁSICA, QUE REQUER QUE O USUÁRIO ESTEJA AUTENTICADO PARA ACESSAR OS DADOS

package com.example.algamoney.api.config;
 

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration // 2- anotação contida no EnableWebSecurity, usamos como lembrete que essa é uma classe de configuração
@EnableWebSecurity // 1- Ativa a Segurança
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	//configurando o processo de autenticação
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication() //de onde vou validar o usuário e senha, nesse caso, em memória
			.withUser("admin").password("admin").roles("ROLE");
			//usuário e senha para autenticação
			//roles para autorização/permissões, porém aqui não está sendo usado
	}

	//configurando permissão de requisições
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests() //autorizações das requisições
				.antMatchers("/categorias").permitAll() //excessão que permite qualquer um acessar os dados de categorias
				.anyRequest().authenticated() //toda requisição precisa estar autenticado, é necessário usuário e senha ser validado
				.and()
			.httpBasic().and() //tipo de autenticação que será feita
			
			//desativa criação de sessão, api não mantém estado
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			//seria para fazer um javascript injection dentro de 1 serviço web, como não temos isso, desativamos
			.csrf().disable();
			
	}
}
*/