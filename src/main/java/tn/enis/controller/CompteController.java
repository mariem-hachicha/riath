package tn.enis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import tn.enis.dto.CompteDTO;
import tn.enis.service.ClientService;
import tn.enis.service.CompteService;

@Controller
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;
    private final ClientService clientService;

    public CompteController(CompteService compteService, ClientService clientService) {
        this.compteService = compteService;
        this.clientService = clientService;
    }

    // ── GET /comptes ──────────────────────────────────────────────────────────
    @GetMapping
    public String listerTous(Model model) {
        List<CompteDTO> comptes = compteService.trouverTous();
        double capitalTotal = comptes.stream().mapToDouble(CompteDTO::getSolde).sum();

        model.addAttribute("comptes",      comptes);
        model.addAttribute("totalComptes", comptes.size());
        model.addAttribute("capitalTotal", capitalTotal);
        model.addAttribute("compteForm",   new CompteDTO());
        model.addAttribute("clients",      clientService.trouverTous());
        return "comptes";
    }

    // ── POST /comptes/creer ───────────────────────────────────────────────────
    @PostMapping("/creer")
    public String creer(@ModelAttribute CompteDTO dto) {
        compteService.creer(dto);
        return "redirect:/comptes";
    }

    // ── GET /comptes/modifier/{rib} ───────────────────────────────────────────
    @GetMapping("/modifier/{rib}")
    public String afficherModifier(@PathVariable Integer rib, Model model) {
        List<CompteDTO> comptes = compteService.trouverTous();
        double capitalTotal = comptes.stream().mapToDouble(CompteDTO::getSolde).sum();

        model.addAttribute("comptes",      comptes);
        model.addAttribute("totalComptes", comptes.size());
        model.addAttribute("capitalTotal", capitalTotal);
        model.addAttribute("compteForm",   compteService.trouverParId(rib));
        model.addAttribute("clients",      clientService.trouverTous());
        return "comptes";
    }

    // ── POST /comptes/modifier ────────────────────────────────────────────────
    @PostMapping("/modifier")
    public String modifier(@ModelAttribute CompteDTO dto) {
        compteService.modifier(dto);
        return "redirect:/comptes";
    }

    // ── DELETE /comptes/delete/{rib}  ← AJAX ─────────────────────────────────
    @DeleteMapping("/delete/{rib}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> supprimerAjax(@PathVariable Integer rib) {
        Map<String, Object> resp = new HashMap<>();
        try {
            compteService.supprimer(rib);
            resp.put("success", true);
            resp.put("message", "Compte supprimé avec succès.");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // ── GET /comptes/client/{cin}  ← AJAX (modal clients.html) ───────────────
    //
    //  RELATION ONE-TO-MANY :
    //  Retourne la liste des comptes JSON pour un client donné.
    //  Utilisé par la fonction showComptes() dans clients.html.
    //
    @GetMapping("/client/{cin}")
    @ResponseBody
    public ResponseEntity<List<CompteDTO>> comptesDuClient(@PathVariable String cin) {
        try {
            List<CompteDTO> comptes = compteService.trouverParClient(cin);
            return ResponseEntity.ok(comptes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ── GET /clients/search  ← Autocomplete jQuery UI ────────────────────────
    // (dans ClientController, mais ajouté ici pour rappel)
    // Voir ClientController.java → @GetMapping("/search")

    // ── POST /comptes/virement ────────────────────────────────────────────────
    @PostMapping("/virement")
    public String virement(@RequestParam Integer source,
                           @RequestParam Integer dest,
                           @RequestParam float  montant,
                           Model model) {
        try {
            compteService.virement(source, dest, montant);
            model.addAttribute("message", "Virement effectué avec succès !");
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage());
        }

        List<CompteDTO> comptes = compteService.trouverTous();
        double capitalTotal = comptes.stream().mapToDouble(CompteDTO::getSolde).sum();
        model.addAttribute("comptes",      comptes);
        model.addAttribute("totalComptes", comptes.size());
        model.addAttribute("capitalTotal", capitalTotal);
        model.addAttribute("compteForm",   new CompteDTO());
        model.addAttribute("clients",      clientService.trouverTous());
        return "comptes";
    }
}