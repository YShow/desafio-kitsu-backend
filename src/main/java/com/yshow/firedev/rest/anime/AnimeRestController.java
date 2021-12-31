package com.yshow.firedev.rest.anime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

/**
 * Controller para rest api de animes
 * @author Yasser Bazzi
 */
@RestController
public class AnimeRestController {
    @Autowired
    private WebClient kitsuWebClient;
    
    public AnimeRestController(WebClient kitsuWebClient) {
	this.kitsuWebClient = kitsuWebClient;
    }
    /**
     * Retorna os animes populares do momento
     * @return JSON
     */
    @GetMapping("/anime/populares")
    public Mono<Object> populares() {
      return kitsuWebClient.get()
	      .uri("/trending/anime")
	      .exchangeToMono(response -> response.bodyToMono(Object.class));
    }
    
    /**
     * Para ver todos os filtros por favor consulte <a href="https://kitsu.docs.apiary.io">kitsu</a>
     * @param filtro Tipo do filtro para retornar na api, exemplo: year
     * @param texto O texto usado para passar no filtro, exemplo: 2021
     * @return JSON
     */
    @GetMapping("/anime/filtrar")
    public Mono<Object> pesquisarPorFiltro(@RequestParam String filtro,@RequestParam String texto) {
 	return kitsuWebClient.get()
 		      .uri("anime?filter[{filtro}]={texto}",filtro,texto)
 		      .exchangeToMono(response -> response.bodyToMono(Object.class));
     }
     /**
      * Pesquisa mangas filtrando por nome
      * @param nomeAnime Nome do anime para pesquisar
      * @return JSON
      */
     @GetMapping("/anime")
     public Mono<Object> pesquisarAnime(@RequestParam(name = "nome") String nomeAnime){	
 	return kitsuWebClient.get()
 		      .uri("anime?filter[text]={nomeAnime}",nomeAnime)
 		      .exchangeToMono(response -> response.bodyToMono(Object.class));
     }
     
     /**
      * Pesquisa um episodio pelo nome do anime
      * @param nomeAnime Nome do anime
      * @param episodio Numero do episodio para procurar
      * @return JSON
      */
     @GetMapping("/anime/episodio")
     public Mono<JsonNode> pesquisarAnimeEpisodio(@RequestParam(name = "nome") String nomeAnime,
	     @RequestParam(name = "ep") Integer episodio) {
	 /* retornamos o primeiro anime na pesquisa por nome
	  * para nao termos que retornar o json inteiro usaremos a json api
	  * para apenas nos mandar o id do anime
	  */
	 final var resposta = kitsuWebClient.get().uri("anime?fields[anime]=id&filter[text]={nomeAnime}", nomeAnime)
		 .retrieve().bodyToMono(JsonNode.class);
	 // pegamos a lista JSON
	 final var animes = resposta.block();
	 if (animes != null) {
	     /*
	      * o id é uma key para identificar o anime na api
	      */
	     final int id = filtrarId(animes);
	     /*
	      * Em vez de usar uma logica propria sera usado a json api para ver o episodio
	      * https://kitsu.docs.apiary.io/#introduction/json:api/filtering-and-search. Na
	      * api de episodios todo json possui a key "number" onde é identificado o
	      * episodio em questao
	      */
	     return kitsuWebClient.get().uri("anime/{id}/episodes?filter[number]={ep}", id, episodio)
		     .retrieve().bodyToMono(JsonNode.class);

	 }

	 return resposta;
     }
     
    
    private static final int filtrarId(final JsonNode animes)
    {
	/*
	 * O json retornado pela api utiliza a estrutura data[array]{valores}
	 *  no caso precisamos apenas do id
	 */
	return animes.get("data").get(0)
		      .get("id").asInt();
    }
}
