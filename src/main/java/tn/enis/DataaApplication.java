package tn.enis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.transaction.Transactional;
import tn.enis.dao.ClientRepository;
import tn.enis.dao.CompteRepository;
import tn.enis.entity.Client;
import tn.enis.entity.Compte;

@SpringBootApplication
public class DataaApplication implements CommandLineRunner {

    @Autowired private ClientRepository clientRepository;
    @Autowired private CompteRepository compteRepository;

    public static void main(String[] args) {
        SpringApplication.run(DataaApplication.class, args);
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (clientRepository.count() == 0) {
            Client c1 = new Client("12345678", "Hachicha", "Ahmed");
            Client c2 = new Client("87654321", "Mezghani", "Sarra");
            Client c3 = new Client("11223344", "Belghith", "Mohamed");
            clientRepository.save(c1);
            clientRepository.save(c2);
            clientRepository.save(c3);

            compteRepository.save(new Compte(1000f, c1));
            compteRepository.save(new Compte(2500f, c1));
            compteRepository.save(new Compte(500f,  c2));
            compteRepository.save(new Compte(3000f, c3));

            System.out.println("=== Données initiales insérées ===");
        } else {
            System.out.println("=== Données déjà présentes ===");
        }
        clientRepository.findAll().forEach(System.out::println);
        compteRepository.findAll().forEach(System.out::println);
    
}
}



// Controller → Service → Repository → Base de données

//👉 Le Service n’est pas utilisé ici, on appelle directement le Repository

//Pourquoi ? Parce que c’est juste pour initialiser rapidement (cas simple