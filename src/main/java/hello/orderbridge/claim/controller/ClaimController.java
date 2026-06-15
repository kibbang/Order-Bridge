package hello.orderbridge.claim.controller;

import hello.orderbridge.claim.domain.Claim;
import hello.orderbridge.claim.dto.CancelRequest;
import hello.orderbridge.claim.dto.ExchangeRequest;
import hello.orderbridge.claim.dto.ReturnRequest;
import hello.orderbridge.claim.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping()
    public String getClaimList(Model model) {
        List<Claim> claimList = claimService.getClaimList();
        model.addAttribute("claims", claimList);
        return "claim/list";
    }

    @GetMapping("/{id}")
    public String getClaim(@PathVariable Long id, Model model) {
        Claim claim = claimService.getClaim(id);
        model.addAttribute("claim", claim);
        return "claim/detail";
    }

    @PostMapping("/cancel")
    public String cancelItem(@ModelAttribute CancelRequest request) {
        claimService.createCancel(request);

        return "redirect:/claims";
    }

    @PostMapping("/return")
    public String returnItem(@ModelAttribute ReturnRequest request) {

        claimService.createReturn(request);

        return "redirect:/claims";
    }

    @PostMapping("/exchange")
    public String exchangeItem(@ModelAttribute ExchangeRequest request) {

        claimService.createExchange(request);

        return "redirect:/claims";
    }

    @PostMapping("/{id}/approve")
    public String approveClaim(@PathVariable Long id) {

        claimService.approveClaim(id);

        return "redirect:/claims";
    }

    @PostMapping("/{id}/reject")
    public String rejectClaim(@PathVariable Long id) {

        claimService.rejectClaim(id);

        return "redirect:/claims";
    }

    @PostMapping("/{id}/complete")
    public String completeClaim(@PathVariable Long id) {

        claimService.completeClaim(id);

        return "redirect:/claims";
    }
}
