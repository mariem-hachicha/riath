package tn.enis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.enis.dao.ClientRepository;
import tn.enis.dao.CompteRepository;
import tn.enis.dto.CompteDTO;
import tn.enis.entity.Client;
import tn.enis.entity.Compte;

@Service
@Transactional
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;

    public CompteService(CompteRepository compteRepository,
                         ClientRepository clientRepository) {
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
    }

    // ── Conversion Entity → DTO ───────────────────────────────────────────────
    //
    //  RELATION MANY-TO-ONE :
    //  Chaque Compte possède un champ  @ManyToOne  Client client.
    //  On récupère le CIN et le nom complet du client pour les mettre dans le DTO.
    //
    public CompteDTO toDTO(Compte compte) {
        String cin          = compte.getClient().getCin();
        String nomComplet   = compte.getClient().getNom() + " " + compte.getClient().getPrenom();
        return new CompteDTO(compte.getRib(), compte.getSolde(), cin, nomComplet);
    }

    // ── Conversion DTO → Entity ───────────────────────────────────────────────
    private Compte toEntity(CompteDTO dto) {
        Client client = clientRepository.findById(dto.getClientCin())
            .orElseThrow(() -> new RuntimeException("Client introuvable : cin=" + dto.getClientCin()));

        Compte compte = new Compte();
        if (dto.getRib() != null) compte.setRib(dto.getRib());
        compte.setSolde(dto.getSolde());
        compte.setClient(client);   // ← RELATION : on lie le Compte à son Client
        return compte;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public CompteDTO creer(CompteDTO dto) {
        Compte compte = toEntity(dto);
        return toDTO(compteRepository.save(compte));
    }

    public CompteDTO modifier(CompteDTO dto) {
        Compte existant = compteRepository.findById(dto.getRib())
            .orElseThrow(() -> new RuntimeException("Compte introuvable : rib=" + dto.getRib()));

        // On peut changer le solde et le client propriétaire
        Client client = clientRepository.findById(dto.getClientCin())
            .orElseThrow(() -> new RuntimeException("Client introuvable : cin=" + dto.getClientCin()));

        existant.setSolde(dto.getSolde());
        existant.setClient(client);   // ← RELATION : changement de propriétaire possible
        return toDTO(compteRepository.save(existant));
    }

    public void supprimer(Integer rib) {
        if (!compteRepository.existsById(rib)) {
            throw new RuntimeException("Compte introuvable : rib=" + rib);
        }
        compteRepository.deleteById(rib);
    }

    public CompteDTO trouverParId(Integer rib) {
        Compte compte = compteRepository.findById(rib)
            .orElseThrow(() -> new RuntimeException("Compte introuvable : rib=" + rib));
        return toDTO(compte);
    }

    public List<CompteDTO> trouverTous() {
        return compteRepository.findAll()
                               .stream()
                               .map(this::toDTO)
                               .collect(Collectors.toList());
    }

    // ── Comptes par client (ONE-TO-MANY) ──────────────────────────────────────
    //
    //  Utilisé par :  GET /comptes/client/{cin}  (appelé depuis le modal AJAX)
    //  CompteRepository doit avoir :
    //    List<Compte> findByClientCin(String cin);
    //
    public List<CompteDTO> trouverParClient(String cin) {
        return compteRepository.findByClientCin(cin)
                               .stream()
                               .map(this::toDTO)
                               .collect(Collectors.toList());
    }

    // ── Virement entre deux comptes ───────────────────────────────────────────
    public void virement(Integer ribSource, Integer ribDest, float montant) {
        Compte source = compteRepository.findById(ribSource)
            .orElseThrow(() -> new RuntimeException("Compte source introuvable"));
        Compte dest   = compteRepository.findById(ribDest)
            .orElseThrow(() -> new RuntimeException("Compte destination introuvable"));

        if (source.getSolde() < montant) {
            throw new RuntimeException("Solde insuffisant");
        }

        source.setSolde(source.getSolde() - montant);
        dest.setSolde(dest.getSolde() + montant);

        compteRepository.save(source);
        compteRepository.save(dest);
    }
}