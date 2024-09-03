package com.egov.socialservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    WebClient webClient;

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
           credential.setCitizenid(UUID.randomUUID());
           credential.setPassword(String.valueOf((int)(Math.random()*1000000)));

           redisTemplate.opsForValue().set(credential.getCitizenid().toString(), credential.getPassword());

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
    public ResponseEntity<String> getdob(@PathVariable String citizenid)
    {

        Mono<Date> responseMono = webClient.get().retrieve().bodyToMono(Date.class); // ASYNCHRONOUS

        final Date[] finalResponse = {null};

        responseMono.subscribe(
                response -> {
                    log.info(response+" from the social service");
                    finalResponse[0] = response;
                },
                error ->
                {
                    log.info("error processing the response "+error);
                });

        return  ResponseEntity.ok("DOB extraction initiated"); // INCORRECT APPROACH

    }

}
