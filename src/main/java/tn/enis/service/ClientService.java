package tn.enis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.enis.dao.ClientRepository;
import tn.enis.dto.ClientDTO;
import tn.enis.entity.Client;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // ── Conversion Entity → DTO ───────────────────────────────────────────────
    public ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO(client.getCin(), client.getNom(), client.getPrenom());

        // RELATION ONE-TO-MANY :
        // Client possède List<Compte> comptes (mappedBy="client")
        // On compte le nombre de comptes pour l'afficher dans le tableau
        int nb = (client.getComptes() != null) ? client.getComptes().size() : 0;
        dto.setNbComptes(nb);

        return dto;
    }

    private Client toEntity(ClientDTO dto) {
        return new Client(dto.getCin(), dto.getNom(), dto.getPrenom());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public ClientDTO creer(ClientDTO dto) {
        if (clientRepository.existsById(dto.getCin())) {
            throw new RuntimeException("Un client avec ce CIN existe déjà : " + dto.getCin());
        }
        return toDTO(clientRepository.save(toEntity(dto)));
    }

    public ClientDTO modifier(ClientDTO dto) {
        Client existant = clientRepository.findById(dto.getCin())
            .orElseThrow(() -> new RuntimeException("Client introuvable : cin=" + dto.getCin()));
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        return toDTO(clientRepository.save(existant));
    }

    public void supprimer(String cin) {
        if (!clientRepository.existsById(cin)) {
            throw new RuntimeException("Client introuvable : cin=" + cin);
        }
        clientRepository.deleteById(cin);
    }

    public ClientDTO trouverParId(String cin) {
        Client client = clientRepository.findById(cin)
            .orElseThrow(() -> new RuntimeException("Client introuvable : cin=" + cin));
        return toDTO(client);
    }

    public List<ClientDTO> trouverTous() {
        return clientRepository.findAll()
                               .stream()
                               .map(this::toDTO)
                               .collect(Collectors.toList());
    }

    public List<ClientDTO> rechercherParTerme(String terme) {
        return clientRepository
            .findByCinContainingIgnoreCaseOrNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                terme, terme, terme)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}