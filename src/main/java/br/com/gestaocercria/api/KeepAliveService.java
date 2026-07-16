package br.com.gestaocercria.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    @Value("${PING_URL:http://localhost:8080/ping}")
    private String pingUrl;

    // Executa a cada 14 minutos (840000 ms)
    @Scheduled(fixedRate = 840000) 
    public void manterAcordado() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            String resposta = restTemplate.getForObject(pingUrl, String.class);
            System.out.println("Ping executado no link: " + pingUrl + " | Resposta: " + resposta);
            
        } catch (Exception e) {
            System.out.println("Erro ao pingar: " + e.getMessage());
        }
    }
}
