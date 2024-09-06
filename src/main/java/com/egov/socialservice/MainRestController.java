package com.egov.socialservice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("webClient_2")
    WebClient webClient;

    @Autowired
    @Qualifier("webClient_3")
    WebClient webClient_obs;

    @Autowired
    CredentialRepository credentialRepository;

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);


    @PostMapping("/save")
    public String saveObj(@RequestParam("key") String key, @RequestParam("value") String value)
    {
        redisTemplate.opsForValue().set(key, value);
        return "success";
    }

    @GetMapping("/register")
    public ResponseEntity<Credential> register()
    {
           Credential credential = new Credential();
           credential.setId(UUID.randomUUID());
           credential.setPassword(String.valueOf((int)(Math.random()*1000000)));
           credentialRepository.save(credential);
           //redisTemplate.opsForValue().set(credential.getCitizenid().toString(), credential.getPassword());

           return  ResponseEntity.ok(credential);
    }

    @GetMapping("/login")
     public ResponseEntity<String> login(@RequestParam("citizenid") UUID citizenid, @RequestParam("password") String password)
    {
        String passwordFromRedis = (String) redisTemplate.opsForValue().get(citizenid.toString());
        if(passwordFromRedis.equals(password))
        {
            return ResponseEntity.ok().header("Authorization","VGHJUHGJ543534534564554").body("AUTHENTICATED");
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getdob/{citizenid}")
    public ResponseEntity<String> getdob(@PathVariable UUID citizenid)
    {

        Mono<Date> responseMono = webClient.post()
                .body(citizenid, UUID.class) // Set the request body as UUID
                .retrieve()
                .bodyToMono(Date.class); // ASYNCHRONOUS

        final Date[] finalResponse = {null};

        responseMono.subscribe(
                response -> {
                    log.info(response+" from the social service");
                    finalResponse[0] = response;
                    //redisTemplate.opsForValue().set(credential.getCitizenid().toString(), credential.getPassword());

                },
                error ->
                {
                    log.info("error processing the response "+error);
                });

        return  ResponseEntity.ok("DOB extraction initiated"); // INCORRECT APPROACH
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealthStatus(HttpServletRequest request)
    {
        Optional<String> healthStatusCookie = Optional.ofNullable(request.getHeader("health_status_cookie"));
        if(healthStatusCookie.isEmpty())
        {
            Mono<String> responseMono = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class); // ASYNCHRONOUS

            //final String[] finalResponse = {null};
            String cookie =  String.valueOf((int)(Math.random()*1000000));

            responseMono.subscribe(
                    response1 -> {
                        log.info(response1+" from the health service");
                        //finalResponse[0] = response;
                        redisTemplate.opsForValue().set(cookie, response1);
                    },
                    error1 ->
                    {
                        log.info("error processing the response "+error1);
                    });

            // SENDING ANOTHER PARALLEL REQUEST

            Mono<String> responseMono_2 = webClient_obs.get()
                    .retrieve()
                    .bodyToMono(String.class); // ASYNCHRONOUS

            responseMono_2.subscribe(
                    response2 -> {
                        log.info(response2+" from the observable service");
                        //finalResponse[0] = response2;
                    },
                    error2 ->
                    {
                        log.info("error processing the response "+error2);
                    });



            return  ResponseEntity.ok().header("health_status_cookie", cookie).body("Status Request Initiated");
        }
        else
        {
             String cookie = request.getHeader("health_status_cookie");
             String response = (String)redisTemplate.opsForValue().get(cookie);

             if(response == null)
             {
                 return ResponseEntity.notFound().build();
             }
             else
             {
                 return ResponseEntity.ok().body(response);
             }
        }
    }





}
