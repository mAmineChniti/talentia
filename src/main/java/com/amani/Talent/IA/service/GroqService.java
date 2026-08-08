package com.amani.Talent.IA.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class GroqService {


    @Value("${groq.api.key}")
    private String apiKey;


    @Value("${groq.url}")
    private String url;


    @Value("${groq.model}")
    private String model;



    private final WebClient webClient;

    private final ObjectMapper objectMapper;



    public GroqService(WebClient webClient) {

        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();

    }




    public String analyzeCandidate(

            String cvText,
            String linkedin,
            String github,
            String motivationLetter,
            String poste

    ) {


        String prompt = """

Tu es un recruteur IT senior spécialisé dans le recrutement des développeurs.

Tu dois analyser un candidat comme dans un vrai processus RH.

Tu dois être :
- objectif
- critique
- basé uniquement sur les informations fournies
- incapable d'inventer des informations


==============================
POSTE À POURVOIR
==============================

%s



==============================
CV DU CANDIDAT
==============================

%s



==============================
LINKEDIN
==============================

%s



==============================
GITHUB
==============================

%s



==============================
LETTRE DE MOTIVATION
==============================

%s



==============================
ANALYSE OBLIGATOIRE
==============================


1) Analyse du poste :

Détermine :

- titre du poste
- niveau demandé
- compétences obligatoires
- expérience demandée


2) Analyse du candidat :

Identifie :

- niveau réel :
  Etudiant / Junior / Confirmé / Senior

- diplôme

- expérience professionnelle réelle


Attention :

Les stages ne doivent pas être considérés comme une expérience professionnelle complète.

Les projets académiques ne remplacent pas une expérience entreprise.


3) Analyse expérience :

Calcule :

- années expérience totale
- années expérience pertinente


Règle :

Stage PFE = expérience partielle.

Projet personnel = preuve technique uniquement.


4) Analyse compétences :

Pour chaque compétence demandée :

Classe :

- Maîtrisée :
preuve par expérience professionnelle ou projet concret.

- Mentionnée :
présente dans CV mais sans preuve suffisante.

- Absente :


5) Analyse projets :

Évalue :

- complexité
- technologies
- impact
- qualité technique


6) Analyse Github :

Évalue :

- activité
- qualité des dépôts
- README
- projets pertinents


Github absent n'est pas éliminatoire mais doit être signalé.


7) Analyse motivation :

Évalue :

- personnalisation
- compréhension du poste
- cohérence.



==============================
CALCUL SCORE
==============================


Niveau et adéquation poste : 25%%

Compétences techniques : 30%%

Expérience pertinente : 25%%

Projets et preuves : 10%%

Motivation et cohérence : 10%%



==============================
INTERPRETATION SCORE
==============================


90-100 :
Excellent candidat.


75-89 :
Très bon profil.


60-74 :
Profil intéressant avec réserves.


40-59 :
Profil faible.


0-39 :
Profil non adapté.



IMPORTANT :

Un étudiant peut avoir un bon score pour un poste junior.

Mais ne donne jamais un score élevé uniquement grâce aux diplômes
ou à une liste de technologies.


==============================
FORMAT REPONSE
==============================


Retourne uniquement un JSON valide.

Aucun texte avant ou après.


{
"score":0,
"stars":0,

"niveau_candidat":"",
"niveau_poste":"",

"compatibilite_niveau":"",

"titre_analyse":"",

"diplome_analyse":"",

"annees_experience_totale":0,
"annees_experience_pertinente":0,

"experience_analyse":"",

"competences_exigees_match":[],

"competences_manquantes":[],

"competences_bonus":[],

"competences_score":0,

"github_analyse":"",

"github_score":0,

"linkedin_analyse":"",

"motivation_analyse":"",

"coherence_profil":"",

"forces":[],

"faiblesses":[],

"risques_recrutement":"",

"points_a_verifier_en_entretien":[],

"feedback":"",

"recommendation":"ACCEPTER / ENTRETIEN / REFUSER"

}


""".formatted(

                poste != null ? poste : "Non spécifié",
                cvText,
                linkedin != null ? linkedin : "Absent",
                github != null ? github : "Absent",
                motivationLetter != null ? motivationLetter : "Absente"

        );



        return askGroq(prompt);

    }






    public String askGroq(String prompt) {


        try {


            Map<String,Object> request = new HashMap<>();


            request.put(
                    "model",
                    model
            );


            request.put(
                    "temperature",
                    0.0
            );


            request.put(
                    "max_tokens",
                    3000
            );



            List<Map<String,String>> messages =
                    new ArrayList<>();



            messages.add(
                    Map.of(
                            "role",
                            "system",
                            "content",
                            """
                            Tu es un recruteur IT senior.
                            Tu réponds uniquement en JSON valide.
                            """
                    )
            );



            messages.add(
                    Map.of(
                            "role",
                            "user",
                            "content",
                            prompt
                    )
            );



            request.put(
                    "messages",
                    messages
            );



            String response =

                    webClient.post()

                            .uri(url)

                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .bodyValue(request)

                            .retrieve()

                            .bodyToMono(String.class)

                            .block();



            System.out.println(
                    "========= GROQ RESPONSE ========="
            );


            System.out.println(response);



            return extractContent(response);



        }
        catch(Exception e){


            e.printStackTrace();


            return """
            {
              "score":0,
              "stars":0,
              "feedback":"Erreur API Groq",
              "recommendation":"REFUSER"
            }
            """;

        }

    }






    private String extractContent(String response){


        try {


            JsonNode root =
                    objectMapper.readTree(response);



            String content =
                    root
                            .path("choices")
                            .get(0)
                            .path("message")
                            .path("content")
                            .asText();



            return content
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();



        }
        catch(Exception e){

            return response;

        }


    }



}