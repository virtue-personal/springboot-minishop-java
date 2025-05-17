package com.virtue.app.controller;

import com.virtue.app.domain.Item;
import com.virtue.app.repository.CommentRepository;
import com.virtue.app.repository.ItemRepository;
import com.virtue.app.service.ItemService;
import com.virtue.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final CommentRepository commentRepository;

    @GetMapping("/list")
    String listRedirect() {
        return "redirect:/list/1";
    }

    @GetMapping("/list/{pageNum}")
    String getListPage(Model model, 
                      @PathVariable Integer pageNum,
                      @RequestParam(required = false, defaultValue = "desc") String sort,
                      @RequestParam(required = false) String searchText) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 6, Sort.by(direction, "id"));
        
        Page<Item> result;
        if (searchText != null && !searchText.trim().isEmpty()) {
            result = itemRepository.searchByTitleWithPaging(searchText.trim(), pageRequest);
        } else {
            result = itemRepository.findPageBy(pageRequest);
        }

        model.addAttribute("items", result);
        model.addAttribute("currentSort", sort);
        model.addAttribute("searchText", searchText);
        return "list";
    }

    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    String writePage() {
        return "write.html";
    }

    @PostMapping("/write")
    @PreAuthorize("isAuthenticated()")
    String writeItem(@RequestParam String title,
                    @RequestParam Integer price,
                    @RequestParam(required = false) String img) {
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setImgUrl(img);
        
        itemRepository.save(item);
        return "redirect:/list";
    }

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, 
                 @RequestParam(required = false, defaultValue = "1") Integer page,
                 @RequestParam(required = false, defaultValue = "desc") String sort,
                 Model model) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            model.addAttribute("page", page);
            model.addAttribute("sort", sort);
            model.addAttribute("comments", commentRepository.findByItemIdOrderByCreatedAtDesc(id));
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

    @PostMapping("/search")
    String search(@RequestParam String searchText,
                 @RequestParam(required = false, defaultValue = "1") Integer page,
                 @RequestParam(required = false, defaultValue = "desc") String sort,
                 Model model) {
        
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page - 1, 6, Sort.by(direction, "id"));
        
        Page<Item> result = itemRepository.searchByTitleWithPaging(searchText.trim(), pageRequest);
        
        model.addAttribute("items", result);
        model.addAttribute("currentSort", sort);
        model.addAttribute("searchText", searchText);
        
        return "search-result";
    }

    @GetMapping("/search")
    String searchPage(@RequestParam String searchText,
                     @RequestParam(required = false, defaultValue = "1") Integer page,
                     @RequestParam(required = false, defaultValue = "desc") String sort,
                     Model model) {
        
        return search(searchText, page, sort, model);
    }
}