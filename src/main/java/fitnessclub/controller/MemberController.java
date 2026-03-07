package fitnessclub.controller;

import fitnessclub.model.Member;
import fitnessclub.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.getAllMembers());
        return "members";
    }

    @PostMapping("/add")
    public String addMember(@RequestParam String name, @RequestParam String email, Model model) {
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Name is required");
            return "members";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("errorMessage", "Invalid email format");
            return "members";
        }
        Member member = new Member(null, name, email);
        memberService.add(member);
        return "redirect:/members";
    }

    @PostMapping("/update")
    public String updateMember(@RequestParam Long id, @RequestParam String name, @RequestParam String email, Model model) {
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Name is required");
            return "members";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("errorMessage", "Invalid email format");
            return "members";
        }
        Member member = memberService.get(id);
        if (member == null) {
            model.addAttribute("errorMessage", "Member not found");
            return "members";
        }
        member.setName(name);
        member.setEmail(email);
        memberService.update(id, member);
        return "redirect:/members";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam Long id) {
        if (memberService.get(id) == null) {
            return "redirect:/members?error=Member+not+found";
        }
        memberService.delete(id);
        return "redirect:/members";
    }
}