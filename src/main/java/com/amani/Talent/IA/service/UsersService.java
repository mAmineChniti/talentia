package com.amani.Talent.IA.service;


import com.amani.Talent.IA.config.CloudinaryConfig;
import com.amani.Talent.IA.entity.Role;
import com.amani.Talent.IA.entity.users;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;



@Service
@RequiredArgsConstructor
public class UsersService {



    private final UsersRepository usersRepository;


    private final com.amani.Talent.IA.config.CloudinaryService cloudinaryService;







    // =====================================
    // Créer utilisateur avec photo profil
    // =====================================


    public users createUser(users user) {

        if (usersRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Mot de passe obligatoire");
        }

        // Image par défaut
        user.setProfileImageUrl(
                "https://res.cloudinary.com/dfmn0gj7e/image/upload/v1786043575/profile_images/whduoj9hjjztmcxcoylo.png"
        );

        // Rôle par défaut
        if (user.getRole() == null) {
            user.setRole(Role.CANDIDATE);
        }

        return usersRepository.save(user);
    }




    public users updateProfilePhoto(Integer id, MultipartFile image) {

        users user = getUserById(id);

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Veuillez sélectionner une image.");
        }

        String imageUrl = cloudinaryService.uploadProfileImage(image);

        user.setProfileImageUrl(imageUrl);

        return usersRepository.save(user);
    }









    // =====================================
    // Récupérer tous les utilisateurs
    // =====================================


    public List<users> getAllUsers(){


        return usersRepository.findAll();

    }








    // =====================================
    // Récupérer utilisateur par ID
    // =====================================


    public users getUserById(
            Integer id
    ){


        return usersRepository.findById(id)

                .orElseThrow(
                        () -> new RuntimeException(
                                "Utilisateur introuvable"
                        )
                );

    }








    // =====================================
    // Récupérer par Email
    // =====================================


    public users getUserByEmail(
            String email
    ){


        return usersRepository.findByEmail(email)

                .orElseThrow(
                        () -> new RuntimeException(
                                "Email introuvable"
                        )
                );

    }








    // =====================================
    // Vérifier Email
    // =====================================


    public boolean existsByEmail(
            String email
    ){


        return usersRepository.existsByEmail(
                email
        );

    }








    // =====================================
    // Modifier utilisateur
    // =====================================


    public users updateUser(

            Integer id,

            users newUser,

            MultipartFile image

    ){


        users user =
                getUserById(id);






        user.setName(
                newUser.getName()
        );


        user.setLastname(
                newUser.getLastname()
        );



        user.setEmail(
                newUser.getEmail()
        );



        user.setCity(
                newUser.getCity()
        );



        user.setCountry(
                newUser.getCountry()
        );



        user.setTelephone(
                newUser.getTelephone()
        );



        user.setAboutme(
                newUser.getAboutme()
        );



        user.setProfession(
                newUser.getProfession()
        );



        user.setEntreprise(
                newUser.getEntreprise()
        );



        user.setPosteActuel(
                newUser.getPosteActuel()
        );



        user.setNiveauExperience(
                newUser.getNiveauExperience()
        );



        user.setCvUrl(
                newUser.getCvUrl()
        );



        user.setLinkedinUrl(
                newUser.getLinkedinUrl()
        );



        user.setGithubUrl(
                newUser.getGithubUrl()
        );









        // Modifier password si envoyé

        if(newUser.getPassword() != null
                &&
                !newUser.getPassword().isEmpty()){


            user.setPassword(
                    newUser.getPassword()
            );

        }








        // Modifier photo profil

        if(image != null
                &&
                !image.isEmpty()){


            String imageUrl =
                    cloudinaryService.uploadProfileImage(
                            image
                    );


            user.setProfileImageUrl(
                    imageUrl
            );

        }








        return usersRepository.save(user);

    }








    // =====================================
    // Supprimer utilisateur
    // =====================================


    public void deleteUser(
            Integer id
    ){


        users user =
                getUserById(id);


        usersRepository.delete(user);

    }




    // =====================================
    // Bannir un utilisateur (admin only)
    // L'utilisateur banni devient invisible
    // partout : posts, commentaires, likes,
    // candidatures, formations, etc.
    // =====================================


    public users banUser(
            Integer targetUserId
    ){

        users user = getUserById(targetUserId);

        user.setBanned(true);

        return usersRepository.save(user);

    }



    // =====================================
    // Débannir un utilisateur (admin only)
    // =====================================


    public users unbanUser(
            Integer targetUserId
    ){

        users user = getUserById(targetUserId);

        user.setBanned(false);

        return usersRepository.save(user);

    }



}
