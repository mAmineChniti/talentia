package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.InterviewRequest;
import com.amani.Talent.IA.dto.InterviewResponse;

import com.amani.Talent.IA.service.InterviewService;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InterviewController {



    private final InterviewService interviewService;



    @PostMapping
    public InterviewResponse create(
            @RequestBody InterviewRequest request
    ){

        return interviewService.createInterview(request);

    }
    @GetMapping
    public List<InterviewResponse> getAllInterviews(){

        return interviewService.getAllInterviews();

    }








    // =====================================
    // GET BY ID
    // =====================================

    @GetMapping("/{id}")
    public InterviewResponse getInterviewById(
            @PathVariable Integer id
    ){

        return interviewService.getInterviewById(id);

    }









    // =====================================
    // UPDATE INTERVIEW
    // =====================================

    @PutMapping("/{id}")
    public InterviewResponse updateInterview(
            @PathVariable Integer id,
            @RequestBody InterviewRequest request
    ){

        return interviewService.updateInterview(
                id,
                request
        );

    }








    // =====================================
    // DELETE INTERVIEW
    // =====================================

    @DeleteMapping("/{id}")
    public String deleteInterview(
            @PathVariable Integer id
    ){


        interviewService.deleteInterview(id);


        return "Entretien supprimé avec succès";

    }








    // =====================================
    // CONFIRMATION CANDIDAT
    // =====================================

    @GetMapping("/confirm/{id}")
    public String confirmInterview(
            @PathVariable Integer id
    ){


        interviewService.confirmInterview(id);


        return """
<!DOCTYPE html>
<html lang="fr">

<head>

<meta charset="UTF-8">

<title>Confirmation - Talent AI</title>


<style>


*{
box-sizing:border-box;
}


body{

margin:0;

height:100vh;

display:flex;

align-items:center;

justify-content:center;

background:#f1f5f9;

font-family:'Segoe UI',Arial,sans-serif;

overflow:hidden;

}




.card{

width:480px;

max-height:95vh;

background:white;

border-radius:22px;

box-shadow:
0 15px 40px rgba(0,0,0,0.15);

overflow:hidden;

}



.header{

height:120px;

background:linear-gradient(
135deg,
#2563eb,
#7c3aed
);

display:flex;

flex-direction:column;

align-items:center;

justify-content:center;

color:white;

}



.logo{

font-size:32px;

font-weight:800;

}



.subtitle{

font-size:14px;

margin-top:5px;

}





.content{

padding:25px;

text-align:center;

}




.success{

width:70px;

height:70px;

margin:auto;

border-radius:50%;

background:#dcfce7;

display:flex;

align-items:center;

justify-content:center;

font-size:45px;

color:#16a34a;

}



h1{

font-size:25px;

color:#16a34a;

margin:15px 0;

}




p{

font-size:15px;

color:#475569;

margin:8px 0;

line-height:1.4;

}




.info{


margin-top:20px;

background:#f8fafc;

padding:15px;

border-radius:12px;

text-align:left;


}




.row{


font-size:14px;

margin:8px 0;

color:#334155;


}




.status{


background:#dcfce7;

color:#15803d;

padding:4px 12px;

border-radius:15px;

font-weight:bold;


}




.button{


display:inline-block;

margin-top:20px;

padding:12px 30px;

background:
linear-gradient(
135deg,
#2563eb,
#7c3aed
);


border-radius:10px;

color:white;

text-decoration:none;

font-weight:bold;

font-size:14px;


}




.footer{


height:45px;

background:#f8fafc;

display:flex;

align-items:center;

justify-content:center;

font-size:12px;

color:#64748b;


}



</style>


</head>


<body>



<div class="card">



<div class="header">


<div class="logo">

🚀 Talent AI

</div>


<div class="subtitle">

Plateforme intelligente RH

</div>


</div>






<div class="content">



<div class="success">

✓

</div>



<h1>
Entretien confirmé !
</h1>




<p>

Votre présence à l'entretien a été enregistrée.

</p>



<p>

Merci pour votre intérêt envers 
<b>Talent AI</b>.

</p>





<div class="info">


<div class="row">

👤 <b>Candidat :</b>
Confirmation enregistrée

</div>



<div class="row">

💼 <b>Service :</b>
RH Talent AI

</div>



<div class="row">

📌 <b>Status :</b>

<span class="status">
Confirmé
</span>

</div>



<div class="row">

🤝 <b>Prochaine étape :</b>
Entretien RH

</div>



</div>





<a class="button" href="#">

Retour Talent AI

</a>




</div>





<div class="footer">

© 2026 Talent AI - Recrutement intelligent

</div>




</div>



</body>

</html>

""";
    }





}