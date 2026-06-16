package hello.orderbridge.claim.controller;

import hello.orderbridge.claim.domain.Claim;
import hello.orderbridge.claim.dto.CancelRequest;
import hello.orderbridge.claim.dto.ClaimSearchCondition;
import hello.orderbridge.claim.dto.ExchangeRequest;
import hello.orderbridge.claim.dto.ReturnRequest;
import hello.orderbridge.claim.service.ClaimService;
import hello.orderbridge.enums.claim.ClaimStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping()
    public String getClaimList(
            @ModelAttribute ClaimSearchCondition condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Page<Claim> claimPage = claimService.getClaimList(condition, PageRequest.of(page, size));

        model.addAttribute("claims", claimPage.getContent());
        model.addAttribute("page", claimPage);
        model.addAttribute("condition", condition);
        model.addAttribute("claimStatuses", ClaimStatus.values());

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
