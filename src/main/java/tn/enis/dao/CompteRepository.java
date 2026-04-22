package tn.enis.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.entity.Compte;

public interface CompteRepository extends JpaRepository<Compte, Integer> {

    /**
     * RELATION ONE-TO-MANY (côté Many) :
     *
     * Un Client possède plusieurs Comptes.
     * Cette méthode retrouve tous les comptes appartenant à un client
     * à partir de son CIN.
     *
     * Spring Data JPA génère la requête automatiquement :
     *   SELECT * FROM t_compte WHERE client_cin = ?
     *
     * Utilisée dans :
     *   - CompteService.trouverParClient(cin)
     *   - CompteController  GET /comptes/client/{cin}  (endpoint AJAX modal)
     */
    List<Compte> findByClientCin(String cin);
}