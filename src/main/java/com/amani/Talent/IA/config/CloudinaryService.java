package com.amani.Talent.IA.config;


import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;


import java.util.Map;



@Service
@RequiredArgsConstructor
public class CloudinaryService {



    private final Cloudinary cloudinary;




    public String uploadProfileImage(
            MultipartFile file
    ){


        try {


            Map uploadResult =
                    cloudinary.uploader()
                            .upload(
                                    file.getBytes(),
                                    Map.of(
                                            "folder",
                                            "profile_images"
                                    )
                            );



            return uploadResult
                    .get("secure_url")
                    .toString();



        } catch(Exception e){


            throw new RuntimeException(
                    "Erreur upload image profil : "
                            + e.getMessage()
            );

        }


    }


}