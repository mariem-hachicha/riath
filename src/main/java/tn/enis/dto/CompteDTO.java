package tn.enis.dto;

public class CompteDTO {

    private Integer rib;
    private float   solde;
    private String  clientCin;         // FK → Client.cin
    private String  clientNomComplet;  // affichage uniquement

    public CompteDTO() {}

    public CompteDTO(Integer rib, float solde, String clientCin, String clientNomComplet) {
        this.rib              = rib;
        this.solde            = solde;
        this.clientCin        = clientCin;
        this.clientNomComplet = clientNomComplet;
    }

    public Integer getRib()                          { return rib; }
    public void    setRib(Integer rib)               { this.rib = rib; }

    public float   getSolde()                        { return solde; }
    public void    setSolde(float solde)             { this.solde = solde; }

    public String  getClientCin()                    { return clientCin; }
    public void    setClientCin(String clientCin)    { this.clientCin = clientCin; }

    public String  getClientNomComplet()             { return clientNomComplet; }
    public void    setClientNomComplet(String n)     { this.clientNomComplet = n; }
}