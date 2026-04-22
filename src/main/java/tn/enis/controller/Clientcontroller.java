package tn.enis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import tn.enis.dto.ClientDTO;
import tn.enis.service.ClientService;
import tn.enis.service.CompteService;

@Controller
@RequestMapping("/clients")
public class Clientcontroller {

    private final ClientService ClientService;
    private final CompteService CompteService;

    public Clientcontroller(ClientService clientService, CompteService compteService) {
        this.ClientService = clientService;
        this.CompteService = compteService;
    }

    // ── GET /clients ──────────────────────────────────────────────────────────
    @GetMapping
    public String listerTous(Model model) {
        List<ClientDTO> clients = ClientService.trouverTous();
        long totalComptes = CompteService.trouverTous().size();

        model.addAttribute("clients",      clients);
        model.addAttribute("totalComptes", totalComptes);
        model.addAttribute("clientForm",   new ClientDTO());
        return "clients";
    }

    // ── POST /clients/creer ───────────────────────────────────────────────────
    @PostMapping("/creer")
    public String creer(@ModelAttribute ClientDTO dto) {
        ClientService.creer(dto);
        return "redirect:/clients";
    }

    // ── GET /clients/modifier/{cin} ───────────────────────────────────────────
    @GetMapping("/modifier/{cin}")
    public String afficherModifier(@PathVariable String cin, Model model) {
        List<ClientDTO> clients = ClientService.trouverTous();
        long totalComptes = CompteService.trouverTous().size();

        model.addAttribute("clients",      clients);
        model.addAttribute("totalComptes", totalComptes);
        model.addAttribute("clientForm",   ClientService.trouverParId(cin));
        return "clients";
    }

    // ── POST /clients/modifier ────────────────────────────────────────────────
    @PostMapping("/modifier")
    public String modifier(@ModelAttribute ClientDTO dto) {
        ClientService.modifier(dto);
        return "redirect:/clients";
    }

    // ── DELETE /clients/delete/{cin}  ← AJAX ─────────────────────────────────
    //
    //  La suppression d'un client déclenche (CASCADE) la suppression
    //  de tous ses comptes (RELATION ONE-TO-MANY).
    //  Assurez-vous que @OneToMany(mappedBy="client", cascade=CascadeType.ALL)
    //  est bien présent dans l'entité Client.
    //
    @DeleteMapping("/delete/{cin}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> supprimerAjax(@PathVariable String cin) {
        Map<String, Object> resp = new HashMap<>();
        try {
            ClientService.supprimer(cin);
            resp.put("success", true);
            resp.put("message", "Client supprimé avec succès.");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // ── GET /clients/search?term=xxx  ← Autocomplete jQuery UI ───────────────
    //
    //  Utilisé par la page comptes.html pour l'autocomplete du champ Client.
    //  Retourne JSON :  [ { cin, nom, prenom }, … ]
    //
    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, String>> search(@RequestParam String term) {
        return ClientService.rechercherParTerme(term)
            .stream()
            .map(c -> {
                Map<String, String> item = new HashMap<>();
                item.put("cin",    c.getCin());
                item.put("nom",    c.getNom());
                item.put("prenom", c.getPrenom());
                return item;
            })
            .collect(Collectors.toList());
    }
}