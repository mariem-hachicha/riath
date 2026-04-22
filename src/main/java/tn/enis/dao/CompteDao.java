package tn.enis.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tn.enis.entity.Compte;

@Repository //@Repository : indique à Spring que cette classe gère l’accès à la base de données.
public class CompteDao {

	@PersistenceContext //EntityManager est le cœur de JPA, il permet :
	private EntityManager entityManager;

	public void save(Compte compte) {
		entityManager.persist(compte);
	}
	
	public void update(Compte compte) {
		entityManager.merge(compte);
	}
	
	public void remove(Compte compte) {
		entityManager.remove(compte);
	}
	
	public Compte findById(Integer rib) {
		return entityManager.find(Compte.class, rib); 
	}
	
	public List<Compte> findAll(){
		// JPQL (JPa Query Language), !=SQL, =~ SQL orienté Objet
		//return entityManager.createQuery("select c from Compte c", Compte.class).getResultList();
		//SQL natif
		return entityManager.createNativeQuery("select * from t_compte", Compte.class).getResultList();
	}
	
}
