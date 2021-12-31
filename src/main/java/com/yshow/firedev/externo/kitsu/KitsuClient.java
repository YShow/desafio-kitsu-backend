package com.yshow.firedev.externo.kitsu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Bean para conectar a api <a href="https://kitsu.docs.apiary.io">kitsu</a>
 * @author Yasser Bazz
 */
@Configuration
public class KitsuClient{
    @Bean
    public WebClient kitsuWebClient() {
	/*
	Pela especificacao https://kitsu.docs.apiary.io/#introduction/json:api
	estou utilizando ambos headers para criar o webclient de acordo
	*/
	return WebClient.builder()
	        .baseUrl("https://kitsu.io/api/edge/")
	        .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.api+json")
	        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json")
	        .build();
    }    
}
