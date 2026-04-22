package tn.enis.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enis.entity.Client;

public interface ClientRepository extends JpaRepository<Client, String> {

    // Recherche pour l'autocomplete
    List<Client> findByCinContainingIgnoreCaseOrNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
        String cin, String nom, String prenom);
}