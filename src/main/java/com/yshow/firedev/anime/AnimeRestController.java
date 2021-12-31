package com.yshow.firedev.anime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.yshow.firedev.KitsuClient;

import reactor.core.publisher.Mono;

@RestController
public class AnimeRestController {
    Logger logger = LoggerFactory.getLogger(AnimeRestController.class);
    @GetMapping("/anime/populares")
    Mono<Object> populares() {
      return KitsuClient.createKitsuWebClient().get()
	      .uri("/trending/anime")
	      .exchangeToMono(response -> response.bodyToMono(Object.class));
    }
    
    @GetMapping("/anime/filtrar")
    public Mono<Object> pesquisarPorFiltro(@RequestParam String filtro,@RequestParam String texto) {
 	return KitsuClient.createKitsuWebClient().get()
 		      .uri("anime?filter[{filtro}]={texto}",filtro,texto)
 		      .exchangeToMono(response -> response.bodyToMono(Object.class));
     }
     
     @GetMapping("/anime")
     public Mono<Object> pesquisarAnime(@RequestParam(name = "nome") String nomeAnime){	
 	return KitsuClient.createKitsuWebClient().get()
 		      .uri("anime?filter[text]={nomeAnime}",nomeAnime)
 		      .exchangeToMono(response -> response.bodyToMono(Object.class));
     }
     
     
     @GetMapping("/anime/episodio")
     public Mono<JsonNode> pesquisarAnimeEpisodio(@RequestParam(name = "nome") String nomeAnime,@RequestParam(name = "ep") Integer ep){
	 //retornamos o primeiro anime na pesquisa por nome
 	final var resposta = KitsuClient.createKitsuWebClient().get()
		      .uri("anime?filter[text]={nomeAnime}",nomeAnime)
		      .retrieve()
		      .bodyToMono(JsonNode.class);
 		      // pegamos a lista JSON
		     final var animes = resposta.block();
		      if(animes != null) {
			  /*
			   o id é uma key para identificar o anime na api
			   */
			  final String id = filtrarId(animes);
			  /*Em vez de usar uma logica propria sera usado a json api para ver o episodio
			    https://kitsu.docs.apiary.io/#introduction/json:api/filtering-and-search.
			    Na api de episodios todo json possui a key "number" onde é identificado o episodio em questao
			  */
			  return KitsuClient.createKitsuWebClient().get().uri("anime/{id}/episodes?filter[number]={ep}",id,ep).retrieve()
			      .bodyToMono(JsonNode.class);
			 
		      }
 	
	 return resposta;
     }
     
     
    private static final String filtrarId(final JsonNode animes)
    {
	return animes.get("data").get(0)
		      .get("id").asText();
    }
}
