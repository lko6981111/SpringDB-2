package hello.itemservice.web;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping//GET 요청을 이 메서드에 매핑,
    public String items(@ModelAttribute("itemSearch") ItemSearchCond itemSearch, Model model) {
        //@ModelAttribute 어노테이션은 메서드 파라미터에 바인딩할 객체를 지정
        //"itemSearch"라는 이름으로 전달된 요청 파라미터를 ItemSearchCond 클래스의 인스턴스로 바인딩합니다.
        // 이를 통해 사용자 입력을 객체로 받을 수 있습니다.
        //Model 객체는 컨트롤러에서 뷰로 데이터를 전달하는 데 사용, 이 객체에 데이터를 추가하면, 그 데이터는 뷰에서 접근할 수 있습니다.
        List<Item> items = itemService.findItems(itemSearch);
        model.addAttribute("items", items);
        return "items";//items 메서드는 GET 요청을 처리하며, HTML 페이지의 이름을 반환

        //이 코드는 사용자가 특정 URL로 GET 요청을 보낼 때, ItemSearchCond 객체를 통해 검색 조건을 받아 itemService를 통해 아이템 목록을 조회한 후,
        // 그 결과를 뷰에 전달하여 "items"라는 이름의 HTML 페이지를 렌더링하는 역할을 합니다.
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {//@PathVariable long itemId: 이 어노테이션은 URL 경로에서 itemId 값을 추출하여 메서드의 매개변수로 전달합니다. 여기서는 long 타입의 itemId에 매핑됩니다.
        Item item = itemService.findById(itemId).get();
        model.addAttribute("item", item);
        return "item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemService.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId).get();
        model.addAttribute("item", item);
        return "editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute ItemUpdateDto updateParam) {
        itemService.update(itemId, updateParam);
        return "redirect:/items/{itemId}";
    }

}
