package com.virtue.springbootweb.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final S3Service s3Service;

    // 페이지 없는 기본 요청은 1페이지로 redirect
    @GetMapping("/list")
    String listRedirect() {
        return "redirect:/list/1";
    }

    @GetMapping("/list/{pageNum}")
    String getListPage(Model model, @PathVariable Integer pageNum) {
        Page<Item> result = itemRepository.findPageBy(PageRequest.of(pageNum - 1, 6));
        model.addAttribute("items", result);
        return "list.html";
    }

    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    String write() {
        return "write.html";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String addItem(@RequestParam String title,
                          @RequestParam Integer price,
                          @RequestParam(required = false) String img) {
        itemService.saveItem(title, price, img);
        return "redirect:/list";
    }

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, Model model) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            return "detail.html";
        } else {
            return "redirect:/list";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    String edit(Model model, @PathVariable Long id) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            return "edit.html";
        } else {
            return "redirect:/list";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    String editItem(@RequestParam Long id,
                    @RequestParam String title,
                    @RequestParam Integer price,
                    @RequestParam(required = false) String img) {
        itemService.editItem(id, title, price, img);
        return "redirect:/list";
    }

    @DeleteMapping("/item")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<String> deleteItem(@RequestParam Long id) {
        itemRepository.deleteById(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename) {
        var result = s3Service.createPresignedUrl("test/" + filename);
        System.out.println(result);
        return result;
    }

    @PostMapping("/test1")
    String test(@RequestBody Map<String, Object> body) {
        System.out.println(body.get("name"));
        return "redirect:/list";
    }
}