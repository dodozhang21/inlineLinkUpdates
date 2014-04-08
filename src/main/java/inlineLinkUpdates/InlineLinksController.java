package inlineLinkUpdates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @ModelAttribute("newInlineLink")
    public InlineLink newInlineLink() {
        return new InlineLink();
    }

    @ModelAttribute("sitesForNewInlineLink")
    public List<SelectOption> sitesForNewInlineLink() {
        return Arrays.asList(
                new SelectOption("", "")
                ,new SelectOption("bhg", "bhg")
                ,new SelectOption("parents", "parents")
                ,new SelectOption("recipecom", "recipecom")
        );
    }

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/search")
    public String search(@ModelAttribute("page") InlineLinkUpdatesPage page
            , final Model model) {

        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), null, page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search/page/{pageNumber}")
    public String search(@ModelAttribute("page") InlineLinkUpdatesPage page
                            , @PathVariable int pageNumber
                            , final Model model) {
        page.getPagination().setCurrentPage(pageNumber);
        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), null, page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public String search(@Valid @ModelAttribute("newInlineLink") InlineLink newInlineLink
            ,final BindingResult result
            ,final Model model) {
        if(result.hasErrors()) {
            model.addAttribute("isAdd", true);
            return "inlineLinks";
        }

        inlineLinksService.addInlineLink(newInlineLink);

        return "inlineLinks";
    }

}
