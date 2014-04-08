package inlineLinkUpdates;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InlineLinksService {
    @Autowired
    private InlineLinksRepository inlineLinksRepository;

    public void addInlineLink(InlineLink inlineLink) {
        inlineLinksRepository.addInlineLink(inlineLink);
    }

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
            parameters.put("topic_name", searchCriteria.getTopicName());
        }
        if(StringUtils.isNotBlank(searchCriteria.getTopicUrl())) {
            parameters.put("topic_url", searchCriteria.getTopicUrl());
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
}
