package com.amani.Talent.IA.service;


import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class EmailService {



    private final JavaMailSender mailSender;





    // ==================================================
    // Email simple
    // ==================================================

    public void sendEmail(
            String to,
            String subject,
            String text
    ){

        try {


            MimeMessage message =
                    mailSender.createMimeMessage();



            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );
            helper.setFrom(
                    "amaniyahyaoui20@gmail.com",
                    "Talent AI"
            );


            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(text);



            mailSender.send(message);



        }
        catch(Exception e){

            throw new RuntimeException(
                    "Erreur envoi email : "
                            + e.getMessage()
            );

        }

    }









    // ==================================================
    // Email convocation entretien RH
    // ==================================================


    public void sendInterviewMail(

            String to,

            String candidateName,

            String date,

            String type,

            String meetingLink,

            String location,

            Integer interviewId

    ){



        try {



            MimeMessage message =
                    mailSender.createMimeMessage();




            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );





            helper.setTo(to);



            helper.setSubject(
                    "Convocation entretien - Talent AI"
            );






            String actionButton = "";



            if(type.equalsIgnoreCase("ONLINE")){


                actionButton =
                        """
        
                        <a href="%s"
        
                        style="
                        display:inline-block;
                        background:#2563eb;
                        color:white;
                        padding:15px 30px;
                        border-radius:10px;
                        text-decoration:none;
                        font-weight:bold;
                        font-size:15px;">
        
                        🎥 Rejoindre l'entretien
        
                        </a>
        
                        """
                                .formatted(
                                        meetingLink
                                );


            }






            String html = """

<!DOCTYPE html>

<html>


<head>

<meta charset="UTF-8">


</head>



<body style="

margin:0;

padding:0;

background:#f1f5f9;

font-family:'Segoe UI',Arial,sans-serif;

">





<div style="

padding:40px 10px;

">






<div style="

max-width:650px;

margin:auto;

background:white;

border-radius:20px;

overflow:hidden;

box-shadow:0 10px 30px rgba(0,0,0,0.1);

">






<!-- HEADER -->


<div style="

background:linear-gradient(135deg,#2563eb,#7c3aed);

padding:40px;

text-align:center;

color:white;

">


<h1 style="

margin:0;

font-size:32px;

">

🚀 Talent AI

</h1>



<p style="

margin-top:10px;

font-size:16px;

">

Plateforme intelligente de recrutement

</p>


</div>







<!-- CONTENT -->


<div style="

padding:35px;

">





<h2 style="color:#1e293b;">

Bonjour %s 👋

</h2>





<p style="

font-size:16px;

line-height:1.6;

color:#475569;

">

Nous avons le plaisir de vous informer que votre profil
a été retenu pour un entretien avec notre équipe RH.

</p>





<p style="

font-size:16px;

line-height:1.6;

color:#475569;

">

Cet entretien représente une étape importante
dans votre processus de recrutement chez
<b>Talent AI</b>.

</p>








<!-- INFORMATION CARD -->


<div style="

background:#f8fafc;

border-radius:15px;

padding:25px;

margin:25px 0;

border:1px solid #e2e8f0;

">



<p>

📅

<b>Date :</b>

%s

</p>





<p>

💼

<b>Type :</b>

%s

</p>





<p>

📍

<b>Lieu :</b>

%s

</p>




</div>








<div style="text-align:center;">

%s

</div>







<br>





<div style="

text-align:center;

">


<p>

Merci de confirmer votre présence :

</p>





<a href="http://localhost:8089/api/interviews/confirm/%d"


style="

display:inline-block;

background:#16a34a;

color:white;

padding:15px 30px;

border-radius:10px;

text-decoration:none;

font-weight:bold;

">

✓ Confirmer ma présence

</a>




</div>







<br><br>






<p style="

color:#475569;

font-size:15px;

">

Si vous avez des questions,
notre équipe RH reste à votre disposition.

</p>







<p>

Cordialement,

<br><br>

<b>

L'équipe RH

<br>

Talent AI

</b>

</p>





</div>










<!-- FOOTER -->


<div style="

background:#f8fafc;

padding:25px;

text-align:center;

color:#64748b;

font-size:13px;

">


© 2026 Talent AI

<br>

Solution intelligente de gestion des talents


</div>








</div>







</div>




</body>


</html>





"""
                    .formatted(

                            candidateName,

                            date,

                            type,

                            location == null ? "-" : location,

                            actionButton,

                            interviewId

                    );







            helper.setText(
                    html,
                    true
            );



            mailSender.send(message);



        }
        catch(Exception e){


            throw new RuntimeException(

                    "Erreur envoi mail entretien : "

                            + e.getMessage()

            );

        }



    }
    // ==================================================
// Email Reset Password
// ==================================================

    public void sendResetPasswordMail(

            String to,

            String userName,

            String token

    ){


        try {


            MimeMessage message =
                    mailSender.createMimeMessage();



            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );



            helper.setFrom(
                    "amaniyahyaoui20@gmail.com",
                    "Talent AI"
            );



            helper.setTo(to);



            helper.setSubject(
                    "Réinitialisation de votre mot de passe - Talent AI"
            );




            String html = """

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

</head>


<body style="
background:#f1f5f9;
font-family:Arial,sans-serif;
padding:30px;
">



<div style="
max-width:600px;
margin:auto;
background:white;
border-radius:15px;
padding:30px;
box-shadow:0 5px 20px rgba(0,0,0,0.1);
">



<h1 style="
color:#2563eb;
text-align:center;
">

🚀 Talent AI

</h1>



<h2>
Bonjour %s 👋
</h2>



<p style="
font-size:16px;
color:#475569;
">

Vous avez demandé la réinitialisation de votre
mot de passe.

</p>




<div style="
background:#eff6ff;
padding:20px;
border-radius:10px;
text-align:center;
margin:25px 0;
">


<p>
Votre code de réinitialisation :
</p>


<h2 style="
color:#2563eb;
">

%s

</h2>


</div>





<p style="
color:#475569;
">

Ce code est valable pendant 15 minutes.

</p>




<p>

Si vous n'avez pas demandé cette modification,
ignorez cet email.

</p>




<br>


<p>

Cordialement,

<br>

<b>
L'équipe Talent AI
</b>

</p>



</div>


</body>

</html>


"""
                    .formatted(
                            userName,
                            token
                    );



            helper.setText(
                    html,
                    true
            );



            mailSender.send(message);



        }
        catch(Exception e){


            throw new RuntimeException(

                    "Erreur envoi email reset password : "

                            + e.getMessage()

            );

        }


    }



}