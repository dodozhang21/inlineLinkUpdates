package inlineLinkUpdates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/inlineLinkUpdates")
@SessionAttributes(value = {"distinctSites", "page"})
public class InlineLinksController {
    @Autowired
    private InlineLinksService inlineLinksService;

    @ModelAttribute("distinctSites")
    public List<String> distinctSites() {
        return inlineLinksService.getDistinctSites();
    }

//    @ModelAttribute("searchCriteria")
//    public InlineLink searchCriteria() {
//        InlineLink searchCriteria = new InlineLink();
//        searchCriteria.setTopicTypeId(2);
//        return searchCriteria;
//    }
//
//    @ModelAttribute("pagination")
//    public Pagination pagination() {
//        Pagination pagination = new Pagination();
//        return pagination;
//    }

    @ModelAttribute("page")
    public InlineLinkUpdatesPage page() {
        InlineLinkUpdatesPage page = new InlineLinkUpdatesPage();
        InlineLink searchCriteria = new InlineLink();
        searchCriteria.setTopicTypeId(2);

        Pagination pagination = new Pagination();

        page.setSearchCriteria(searchCriteria);
        page.setPagination(pagination);
        return page;
    }

    @ModelAttribute("numberPerPageChoices")
    public List<SelectOption> numberPerPageChoices() {
        return Arrays.asList(
                new SelectOption("25", "25")
                ,new SelectOption("50", "50")
                ,new SelectOption("100", "100")
                ,new SelectOption("200", "200")
                ,new SelectOption(String.valueOf(Integer.MAX_VALUE), "All")
        );
    }

    @RequestMapping("/")
    public String index(Model model) {


        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/search")
    public String search(@ModelAttribute("page") InlineLinkUpdatesPage page, Model model) {

        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), null, page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search/page/{pageNumber}")
    public String search(@ModelAttribute("page") InlineLinkUpdatesPage page
                            , @PathVariable int pageNumber
                            , Model model) {
        page.getPagination().setCurrentPage(pageNumber);
        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), null, page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

}
