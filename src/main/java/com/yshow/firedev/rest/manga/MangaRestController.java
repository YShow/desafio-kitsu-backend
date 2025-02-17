package com.yshow.firedev.rest.manga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


/**
 * Controller para rest api de mangas
 * @author Yasser Bazzi
 */
@RestController
public class MangaRestController {
    
    @Autowired
    private WebClient kitsuWebClient;

    public MangaRestController(WebClient kitsuWebClient) {
	this.kitsuWebClient = kitsuWebClient;
    }
    /**
     * Retorna os mangas populares do momento
     * @return JSON
     */
    @GetMapping("/manga/populares")
    public Mono<Object> populares() {
      return kitsuWebClient.get()
	      .uri("trending/manga")
	      .exchangeToMono(response -> response.bodyToMono(Object.class));
    }
    /**
     * Para ver todos os filtros por favor consulte <a href="https://kitsu.docs.apiary.io">kitsu</a>
     * @param filtro Tipo do filtro para retornar na api, exemplo: year
     * @param texto O texto usado para passar no filtro, exemplo: 2021
     * @return JSON
     */
    @GetMapping("/manga/filtrar")
   public Mono<Object> pesquisarPorFiltro(@RequestParam String filtro,@RequestParam String texto) {
	return kitsuWebClient.get()
		      .uri("manga?filter[{filtro}]={texto}",filtro,texto)
		      .exchangeToMono(response -> response.bodyToMono(Object.class));
    }
    /**
     * Pesquisa mangas filtrando por nome
     * @param nome Nome do manga para pesquisar
     * @return JSON
     */
    @GetMapping("/manga")
    public Mono<Object> pesquisarPorFiltro(@RequestParam String nome) {
 	return kitsuWebClient.get()
 		      .uri("manga?filter[text]={nome}",nome)
 		      .exchangeToMono(response -> response.bodyToMono(Object.class));
     }
    
    
}
