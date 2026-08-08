package com.amani.Talent.IA.service;


import com.cloudinary.Cloudinary;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.util.Map;



@Service
@RequiredArgsConstructor
public class QrCodeService {



    private final Cloudinary cloudinary;




    public String generateQr(String text) {


        try {


            // Génération du QR Code

            BitMatrix matrix =
                    new MultiFormatWriter()
                            .encode(
                                    text,
                                    BarcodeFormat.QR_CODE,
                                    300,
                                    300
                            );



            // Conversion en image PNG

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();



            MatrixToImageWriter.writeToStream(
                    matrix,
                    "PNG",
                    output
            );



            byte[] image =
                    output.toByteArray();



            // Upload vers Cloudinary

            Map upload =
                    cloudinary.uploader()
                            .upload(
                                    image,
                                    Map.of(
                                            "folder",
                                            "employees_qr"
                                    )
                            );



            // Retour URL image Cloudinary

            return upload
                    .get("secure_url")
                    .toString();



        } catch (Exception e) {


            throw new RuntimeException(
                    "Erreur lors de la génération du QR Code : "
                            + e.getMessage()
            );


        }


    }



}