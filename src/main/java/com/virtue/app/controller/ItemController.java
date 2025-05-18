package com.virtue.app.controller;

import com.virtue.app.domain.Item;
import com.virtue.app.service.ItemService;
import com.virtue.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final S3Service s3Service;

    /**
     * 리스트 첫 페이지로 리디렉션
     */
    @GetMapping("/list")
    public String listRedirect() {
        return "redirect:/list/1";
    }

    /**
     * 상품 목록 페이지 조회
     */
    @GetMapping("/list/{pageNum}")
    public String getListPage(Model model,
                              @PathVariable Integer pageNum,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(required = false) String searchText) {
        Page<Item> result = itemService.getItemPage(pageNum, sort, searchText);
        model.addAttribute("items", result);
        model.addAttribute("currentSort", sort);
        model.addAttribute("searchText", searchText);
        return "list";
    }

    /**
     * 상품 작성 페이지 반환
     */
    @GetMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writePage() {
        return "write.html";
    }

    /**
     * 상품 작성 처리
     */
    @PostMapping("/write")
    @PreAuthorize("isAuthenticated()")
    public String writeItem(@RequestParam String title,
                            @RequestParam Integer price,
                            @RequestParam(required = false) String img) {
        itemService.saveItem(title, price, img);
        return "redirect:/list";
    }

    /**
     * 상품 상세 페이지 반환
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "desc") String sort,
                         Model model) {

        var detail = itemService.getItemDetail(id);
        if (detail.isEmpty()) return "redirect:/list";

        Map<String, Object> detailMap = detail.get();
        model.addAttribute("data", detailMap.get("item"));
        model.addAttribute("comments", detailMap.get("comments"));
        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        return "detail.html";
    }

    /**
     * 상품 수정 페이지 반환
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Long id, Model model) {
        return itemService.getEditPageData(id)
                .map(item -> {
                    model.addAttribute("data", item);
                    return "edit.html";
                })
                .orElse("redirect:/list");
    }

    /**
     * 상품 수정 처리
     */
    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editItem(@RequestParam Long id,
                           @RequestParam String title,
                           @RequestParam Integer price,
                           @RequestParam(required = false) String img) {
        itemService.editItem(id, title, price, img);
        return "redirect:/list";
    }

    /**
     * 상품 삭제 처리
     */
    @DeleteMapping("/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteItem(@RequestParam Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("삭제완료");
    }

    /**
     * Presigned URL 발급
     */
    @GetMapping("/presigned-url")
    @ResponseBody
    public String getURL(@RequestParam String filename) {
        return s3Service.createPresignedUrl("test/" + filename);
    }

    /**
     * 테스트용 바디 수신
     */
    @PostMapping("/test1")
    public String test(@RequestBody Map<String, Object> body) {
        System.out.println(body.get("name"));
        return "redirect:/list";
    }

    /**
     * 상품 검색 처리 (POST 방식)
     */
    @PostMapping("/search")
    public String search(@RequestParam String searchText,
                         @RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "desc") String sort,
                         Model model) {
        Page<Item> result = itemService.searchItems(searchText, page, sort);
        model.addAttribute("items", result);
        model.addAttribute("currentSort", sort);
        model.addAttribute("searchText", searchText);
        return "search-result";
    }

    /**
     * 상품 검색 페이지 (GET 방식)
     */
    @GetMapping("/search")
    public String searchPage(@RequestParam String searchText,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "desc") String sort,
                             Model model) {
        return search(searchText, page, sort, model);
    }
}