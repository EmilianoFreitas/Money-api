package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration // 2- anotação contida no EnableWebSecurity, usamos como lembrete que essa é uma classe de configuração
@EnableWebSecurity // 1- Ativa a Segurança
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true) //habilita segurança nos métodos
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	//configurando o processo de autenticação
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
		/* MODO INICIAL
		auth.inMemoryAuthentication() //de onde vou validar o usuário e senha, nesse caso, em memória
			.withUser("admin").password("admin").roles("ROLE");
			//usuário e senha para autenticação
			//roles para autorização/permissões, porém aqui não está sendo usado
		*/
	}

	//configurando permissão de requisições
	@Override
	public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests() //autorizações das requisições
				.antMatchers("/categorias").permitAll() //excessão que permite qualquer um acessar os dados de categorias
				.anyRequest().authenticated() //toda requisição precisa estar autenticado, é necessário usuário e senha ser validado
				.and()
			//desativa criação de sessão, api não mantém estado
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			//seria para fazer um javascript injection dentro de 1 serviço web, como não temos isso, desativamos
			.csrf().disable();
			
	}
	
	//Reforça que as sessões não devem manter estados
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//Handler que faz a segurança dos métodos com o OAuth2
	@Bean
	public MethodSecurityExpressionHandler creationExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
	
	
}
