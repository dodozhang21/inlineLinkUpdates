package inlineLinkUpdates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InlineLinkUpdatesPage implements Serializable {
    private InlineLink searchCriteria;
    private Pagination pagination;
    private List<InlineLink> results = new ArrayList<InlineLink>();

    public InlineLink getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(InlineLink searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<String> getNumberPerPageChoices() {
        return Arrays.asList("25", "50", "100", "200", "All");
    }

    public List<InlineLink> getResults() {
        return results;
    }

    public void setResults(List<InlineLink> results) {
        this.results = results;
    }
}
