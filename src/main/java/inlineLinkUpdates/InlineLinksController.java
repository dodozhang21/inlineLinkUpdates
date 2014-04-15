package inlineLinkUpdates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/inlineLinkUpdates")
@SessionAttributes(value = {"distinctSites", "page", "editInlineLink"})
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

    @ModelAttribute("orderByChoices")
    public List<SelectOption> orderByChoices() {
        return Arrays.asList(
                new SelectOption("priority asc", "Priority")
                ,new SelectOption("topic_id asc", "Topic ID")
                ,new SelectOption("topic_name asc", "Topic Name")
        );
    }

    @ModelAttribute("newInlineLink")
    public InlineLink newInlineLink() {
        InlineLink newInlineLink = new InlineLink();
        newInlineLink.setPriority(20);
        return newInlineLink;
    }

    @ModelAttribute("editInlineLink")
    public InlineLink editInlineLink() {
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

        page.getPagination().setCurrentPage(1);
        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), page.getOrderBy(), page.getPagination());
        model.addAttribute("results", results);
        model.addAttribute("page", page);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search/page/{pageNumber}")
    public String search(@ModelAttribute("page") InlineLinkUpdatesPage page
                            , @PathVariable int pageNumber
                            , final Model model) {
        page.getPagination().setCurrentPage(pageNumber);
        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), page.getOrderBy(), page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public String add(@Valid @ModelAttribute("newInlineLink") InlineLink newInlineLink
            ,final BindingResult result
            ,final Model model) {
        if(result.hasErrors()) {
            model.addAttribute("isAdd", true);
            return "inlineLinks";
        }

        try {
            inlineLinksService.addInlineLink(newInlineLink);
        } catch(DuplicateKeyException e) {
            result.addError(new ObjectError("topicId",
                String.format(
                        "Inline link already exists for topic name '%s', topic url '%s', site '%s'."
                        ,newInlineLink.getTopicName()
                        ,newInlineLink.getTopicUrl()
                        ,newInlineLink.getSite()
                    )
                )
            );
            model.addAttribute("isAdd", true);
        }

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/delete/{topicId}")
    public String delete(@PathVariable String topicId
            , @ModelAttribute("page") InlineLinkUpdatesPage page
            , final Model model) {

        inlineLinksService.deleteInlineLink(topicId);
        List<InlineLink> results = inlineLinksService.searchInlineLinks(page.getSearchCriteria(), null, page.getPagination());
        model.addAttribute("results", results);

        return "inlineLinks";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/edit/{topicId}")
    public String editLoad(@PathVariable String topicId
            , @ModelAttribute("editInlineLink") InlineLink editInlineLink
            , final Model model) {

        editInlineLink = inlineLinksService.findInlineLinkByTopicId(topicId);
        model.addAttribute("editInlineLink", editInlineLink);

        return "editInlineLink";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit")
    public String edit(@Valid @ModelAttribute("editInlineLink") InlineLink editInlineLink
            ,final BindingResult result
            ,final Model model
            ,HttpServletResponse response) {

        if(result.hasErrors()) {
            response.setStatus(418); //http://en.wikipedia.org/wiki/List_of_HTTP_status_codes#4xx_Client_Error april fools's joke
            return "editInlineLink";
        }

        response.setHeader("topicId", editInlineLink.getTopicId());
        inlineLinksService.updateInlineLink(editInlineLink);
        return "editInlineLink";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/like/{parameter}/{searchTerm}")

    @ResponseBody
    public List<AutoCompleteItem> findLikeInlineLinks(@PathVariable String parameter
            , @PathVariable String searchTerm
            , @RequestParam(value="site", required=true) String site
            , HttpServletResponse response) {

        InlineLink searchCriteria = new InlineLink();
        searchCriteria.setSite(site);
        // make sure the parameter is a property on inlinelink
        if(!InlineLink.hasProperty(parameter)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        return inlineLinksService.findLikeTerms(parameter, searchTerm, searchCriteria);
    }

}
