package inlineLinkUpdates;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InlineLinksService {
    @Autowired
    private InlineLinksRepository inlineLinksRepository;

    public void addInlineLink(InlineLink inlineLink) {
        // default values
        inlineLink.setTopicTypeId(2);
        // fill topic id
        inlineLink.setTopicId(inlineLinksRepository.getNextInlineLinkTopicId());

        inlineLink.setTopicName(StringUtils.trim(inlineLink.getTopicName()));

        // figure out words in topic name
        String[] words = StringUtils.split(inlineLink.getTopicName(), " ");
        inlineLink.setNumWordsInTopic(words.length);

        inlineLinksRepository.addInlineLink(inlineLink);
    }

    @Transactional
    public void deleteInlineLink(String topicId) {
        inlineLinksRepository.deleteInlineLink(topicId);
    }

    public List<InlineLink> searchInlineLinks(InlineLink searchCriteria, String orderBy, Pagination pagination) {
        // convert to map
        Map<String, String> parameters = new HashMap<String, String>();
        if(StringUtils.isNotBlank(searchCriteria.getTopicId())) {
            parameters.put("topic_id", searchCriteria.getTopicId());
        }
        if(StringUtils.isNotBlank(searchCriteria.getTopicName())) {
            parameters.put("topic_name", "%"+searchCriteria.getTopicName()+"%");
        }
        if(StringUtils.isNotBlank(searchCriteria.getTopicUrl())) {
            parameters.put("topic_url", "%"+searchCriteria.getTopicUrl()+"%");
        }
        if(searchCriteria.getPriority() != null) {
            parameters.put("priority", String.valueOf(searchCriteria.getPriority()));
        }
        if(searchCriteria.getTopicTypeId() != null) {
            parameters.put("topic_type_id", String.valueOf(searchCriteria.getTopicTypeId()));
        }
        if(searchCriteria.getNumWordsInTopic() != null) {
            parameters.put("num_words_in_topic", String.valueOf(searchCriteria.getNumWordsInTopic()));
        }
        if(StringUtils.isNotBlank(searchCriteria.getSite())) {
            parameters.put("site", searchCriteria.getSite());
        }

        return inlineLinksRepository.searchInlineLinks(parameters, orderBy, pagination);
    }

    public List<String> getDistinctSites() {
        return inlineLinksRepository.getDistinctSites();
    }

    public InlineLink findInlineLinkByTopicId(String topicId) {
        return inlineLinksRepository.findInlineLinkByTopicId(topicId);
    }

    public void updateInlineLink(InlineLink inlineLink) {
        inlineLinksRepository.updateInlineLink(inlineLink);
    }

    public List<AutoCompleteItem> findLikeTerms(String parameter, String value, InlineLink inlineLink) {
        List<AutoCompleteItem> autoCompleteItems = new ArrayList<AutoCompleteItem>();
        List<InlineLink> results = inlineLinksRepository.findLikeRows(parameter, value, inlineLink);
        if(results != null) {
            for(InlineLink result : results) {
                String propertyValue = result.getStringProperty(parameter);
                String id = "i" + propertyValue.replaceAll("\\W", "");
                autoCompleteItems.add(new AutoCompleteItem(id, propertyValue, propertyValue));
            }
        }
        return autoCompleteItems;
    }
}