package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.service.GroqService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/groq")
@CrossOrigin("*")
public class GroqController {


    private final GroqService groqService;


    public GroqController(GroqService groqService){
        this.groqService = groqService;
    }


    @GetMapping
    public String chat(
            @RequestParam String prompt
    ){

        return groqService.askGroq(prompt);

    }

}
