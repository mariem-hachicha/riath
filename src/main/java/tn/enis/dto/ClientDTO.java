package tn.enis.dto;

public class ClientDTO {

    private String cin;
    private String nom;
    private String prenom;
    private int nbComptes;   // ← RELATION ONE-TO-MANY : nombre de comptes du client

    public ClientDTO() {}

    public ClientDTO(String cin, String nom, String prenom) {
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getCin()             { return cin; }
    public void   setCin(String cin)   { this.cin = cin; }

    public String getNom()             { return nom; }
    public void   setNom(String nom)   { this.nom = nom; }

    public String getPrenom()          { return prenom; }
    public void   setPrenom(String p)  { this.prenom = p; }

    public int    getNbComptes()       { return nbComptes; }
    public void   setNbComptes(int n)  { this.nbComptes = n; }

    public String getNomComplet()      { return nom + " " + prenom; }
}