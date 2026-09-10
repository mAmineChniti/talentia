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
                    "TalentIA"
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



            helper.setFrom(
                    "amaniyahyaoui20@gmail.com",
                    "TalentIA"
            );




            helper.setTo(to);



            helper.setSubject(
                    "Convocation entretien - TalentIA"
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

🚀 TalentIA

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
<b>TalentIA</b>.

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

TalentIA

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


© %d TalentIA

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

                            interviewId,

                            java.time.LocalDate.now().getYear()

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
                    "TalentIA"
            );



            helper.setTo(to);



            helper.setSubject(
                    "Réinitialisation de votre mot de passe - TalentIA"
            );




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

<h1 style="margin:0;font-size:32px;">🚀 TalentIA</h1>

<p style="margin-top:10px;font-size:16px;">Réinitialisation de mot de passe</p>

</div>



<!-- CONTENT -->

<div style="padding:35px;">

<h2 style="color:#1e293b;">Bonjour %s 👋</h2>



<p style="

font-size:16px;

line-height:1.6;

color:#475569;

">

Vous avez demandé la réinitialisation de votre

mot de passe.

</p>



<!-- CODE CARD -->

<div style="

background:#eff6ff;

border-radius:15px;

padding:25px;

margin:25px 0;

text-align:center;

border:1px solid #bfdbfe;

">

<p style="font-size:16px;color:#1e293b;margin-bottom:10px;">

Votre code de réinitialisation :

</p>

<h2 style="

color:#2563eb;

margin:0;

font-size:28px;

letter-spacing:5px;

">

%s

</h2>

</div>



<p style="

color:#475569;

font-size:15px;

">

Ce code est valable pendant 15 minutes.

</p>



<p style="

color:#475569;

font-size:15px;

">

Si vous n'avez pas demandé cette modification,

ignorez cet email.

</p>



<br>



<p>

Cordialement,

<br><br>

<b>

L'équipe RH

<br>

TalentIA

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

© %d TalentIA

<br>

Solution intelligente de gestion des talents

</div>



</div>

</div>



</body>

</html>

"""
                    .formatted(
                            userName,
                            token,
                            java.time.LocalDate.now().getYear()
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

    // ==================================================
    // Email bienvenue employé + QR Code
    // ==================================================

    public void sendWelcomeEmail(
            String to,
            String employeeName,
            String employeeCode,
            String department,
            String position,
            String qrImageUrl
    ) {
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
                    "TalentIA"
            );

            helper.setTo(to);

            helper.setSubject(
                    "Bienvenue chez TalentIA - Votre QR Code de présence"
            );

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
            <div style="padding:40px 10px;">
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
            <h1 style="margin:0;font-size:32px;">🚀 TalentIA</h1>
            <p style="margin-top:10px;font-size:16px;">Bienvenue dans l'équipe !</p>
            </div>

            <!-- CONTENT -->
            <div style="padding:35px;">
            <h2 style="color:#1e293b;">Bonjour %s 👋</h2>

            <p style="font-size:16px;line-height:1.6;color:#475569;">
            Nous avons le plaisir de vous confirmer votre embauche
            chez <b>TalentIA</b>. Vous faites désormais partie de l'équipe !
            </p>

            <!-- INFO CARD -->
            <div style="
            background:#f8fafc;
            border-radius:15px;
            padding:25px;
            margin:25px 0;
            border:1px solid #e2e8f0;
            ">
            <p>💼 <b>Matricule :</b> %s</p>
            <p>🏢 <b>Département :</b> %s</p>
            <p>📌 <b>Poste :</b> %s</p>
            </div>

            <!-- QR CODE -->
            <div style="
            background:#eff6ff;
            border-radius:15px;
            padding:30px;
            margin:25px 0;
            text-align:center;
            border:1px solid #bfdbfe;
            ">
            <p style="font-size:16px;color:#1e293b;margin-bottom:15px;">
            📱 <b>Votre QR Code de présence</b>
            </p>
            <p style="font-size:14px;color:#475569;margin-bottom:20px;">
            Présentez ce code à l'accueil pour pointer votre présence.
            </p>
            <img src="%s"
            alt="QR Code de présence"
            style="
            width:200px;
            height:200px;
            border-radius:15px;
            border:2px solid #e2e8f0;
            " />
            <p style="font-size:13px;color:#64748b;margin-top:15px;">
            Code : %s
            </p>
            </div>

            <p style="color:#475569;font-size:15px;">
            Conservez ce QR code précieusement. Il vous servira
            à pointer votre entrée et votre sortie quotidienne.
            </p>

            <br>

            <p style="color:#475569;font-size:15px;">
            Si vous avez des questions, notre équipe RH reste à votre disposition.
            </p>

            <br>

            <p>
            Cordialement,<br><br>
            <b>L'équipe RH<br>TalentIA</b>
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
            © %d TalentIA<br>
            Solution intelligente de gestion des talents
            </div>
            </div>
            </div>
            </body>
            </html>
            """
                    .formatted(
                            employeeName,
                            employeeCode,
                            department == null ? "-" : department,
                            position == null ? "-" : position,
                            qrImageUrl,
                            employeeCode,
                            java.time.LocalDate.now().getYear()
                    );

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur envoi email bienvenue : "
                            + e.getMessage()
            );
    }

}

}