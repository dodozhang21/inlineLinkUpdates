package inlineLinkUpdates;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class ApplicationTests {
    @Autowired
    private InlineLinksRepository inlineLinksRepository;

    @Test
    public void testAddAndSearchAndDeleteInlineLink() {
        InlineLink inlineLink = new InlineLink();

        final String topicId = "test" + GregorianCalendar.getInstance().getTimeInMillis();
        inlineLink.setTopicId(topicId);
        inlineLink.setTopicName("test");

        inlineLinksRepository.addInlineLink(inlineLink);


        Map<String, String> parameters = new HashMap<String, String>() {
            {
                put("topic_id", topicId);
            }
        };

        List<InlineLink> results = inlineLinksRepository.searchInlineLinks(parameters);

        assertEquals(1, results.size());
        assertEquals(topicId, results.get(0).getTopicId());

        inlineLinksRepository.deleteInlineLink(topicId);

        List<InlineLink> newResults = inlineLinksRepository.searchInlineLinks(parameters);
        assertTrue(newResults.isEmpty());
    }

	@Test
	public void testBuildSql_defaultOrderBy() {
        Map<String, String> parameters = new HashMap<String, String>() {
            {
                put("topic_id", "rbs1345");
                put("topic_name", "baby");
            }
        };

        String sql = inlineLinksRepository.buildSql("select * from mdp_topic", parameters, null);

        assertEquals("select * from mdp_topic where topic_name = ? and topic_id = ? order by topic_name asc", sql);
	}

    @Test
    public void testBuildSql_widthOrderBy() {
        Map<String, String> parameters = new HashMap<String, String>() {
            {
                put("topic_id", "rbs1345");
                put("topic_name", "baby");
            }
        };

        String sql = inlineLinksRepository.buildSql("select * from mdp_topic", parameters, "topic_id desc, topic_name asc");

        assertEquals("select * from mdp_topic where topic_name = ? and topic_id = ? order by topic_id desc, topic_name asc", sql);
    }

    @Test
    public void testPagination() {
        Map<String, String> parameters = new HashMap<String, String>();

        Pagination pagination = new Pagination();

        List<InlineLink> results = inlineLinksRepository.searchInlineLinks(parameters, pagination);

        assertEquals(pagination.getNumberPerPage(), results.size());

        for(InlineLink inlineLink : results) {
            System.out.println(inlineLink);
        }
    }

}