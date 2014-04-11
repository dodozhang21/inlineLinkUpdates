package utils;

import inlineLinkUpdates.InlineLink;
import inlineLinkUpdates.InlineLinksRepository;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SyncMdpTopicContext.class, InlineLinksRepository.class})
public class SyncMdpTopic {
    @Autowired
    @Qualifier("prodJdbcTemplate")
    private JdbcTemplate prodJdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate devJdbcTemplate;

    @Autowired
    private InlineLinksRepository inlineLinksRepository;

    private String site = "recipecom";


//    @Test
    public void doesDevMatchProd() {
//        String sql = "select * from mdp_topic where site = ? and topic_id = '1001man'";
        String sql = "select * from mdp_topic where site = ? order by topic_name, topic_url";

        List<InlineLink> devResults = devJdbcTemplate.query(sql, new Object[] { site }, new InlineLinksRepository.InlineLinkRowMapper());
        List<InlineLink> prodResults = prodJdbcTemplate.query(sql, new Object[] { site }, new InlineLinksRepository.InlineLinkRowMapper());

        assertEquals("total records in dev is the same as prod", devResults.size(), prodResults.size());

        int index = 0;
        for(InlineLink devInlineLink : devResults) {
            InlineLink prodInlineLink = prodResults.get(index);

            assertTrue(String.format("topicId %s in dev matches topicId %s in prod", devInlineLink.getTopicId(), prodInlineLink.getTopicId())
                    , devInlineLink.equals(prodInlineLink));
            index++;
        }
    }


/*    @Test
    @Rollback(false)*/
    public void forSameStuffMakeSurePropertiesMatchProd() {
        String devSyncSql = "";
        String sql = "select * from mdp_topic where site = ?";

        List<InlineLink> results = prodJdbcTemplate.query(sql
                , new Object[] { site }
                , new InlineLinksRepository.InlineLinkRowMapper()
        );

        for(InlineLink inlineLink : results) {

            String escapedTopicName = StringUtils.replace(inlineLink.getTopicName(), "'", "''");

            String updateSql = String.format("update mdp_topic set priority = '%s', topic_type_id = '%s', num_words_in_topic = '%s' where" +
                            " topic_name = '%s' and topic_url = '%s' and site = '%s'"
                    ,inlineLink.getPriority()
                    ,inlineLink.getTopicTypeId()
                    ,inlineLink.getNumWordsInTopic()
                    ,escapedTopicName
                    ,inlineLink.getTopicUrl()
                    ,inlineLink.getSite()
            );
            devSyncSql += updateSql + ";";
            devSyncSql += "\n";
            devJdbcTemplate.update(updateSql);

        }

//        System.out.println(devSyncSql);
    }


/*    @Test
    @Rollback(false)*/
    public void inDevButNotInProd() {
        String devDeleteSql = "";

        String sql = "select * from mdp_topic where site = ?";

        List<InlineLink> results = devJdbcTemplate.query(sql
                , new Object[] { site }
                , new InlineLinksRepository.InlineLinkRowMapper()
        );

        for(InlineLink inlineLink : results) {
            String unique = "select * from mdp_topic where topic_name = ? and topic_url = ? and site =?";
            List<InlineLink> prodResults = prodJdbcTemplate.query(unique
                    , new Object[] {
                            inlineLink.getTopicName(),
                            inlineLink.getTopicUrl(),
                            inlineLink.getSite()
                    }
                    , new InlineLinksRepository.InlineLinkRowMapper()
            );
            String escapedTopicName = StringUtils.replace(inlineLink.getTopicName(), "'", "''");
            if(prodResults.isEmpty()) {
                String deleteContentTopic = String.format("delete from mdp_content_topic where topic_id in (select topic_id from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s')"
                        ,escapedTopicName
                        ,inlineLink.getTopicUrl()
                        ,inlineLink.getSite()
                );
                String deleteTopic = String.format("delete from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s'"
                        , escapedTopicName
                        , inlineLink.getTopicUrl()
                        , inlineLink.getSite()
                );
                devJdbcTemplate.update(deleteContentTopic);
                devJdbcTemplate.update(deleteTopic);
                devDeleteSql += deleteContentTopic + ";";
                devDeleteSql += "\n";
                devDeleteSql += deleteTopic + ";";
                devDeleteSql += "\n";
            }
        }

//        System.out.println(devDeleteSql);
    }



/*    @Test
    @Rollback(false)*/
    public void missingInDevFromProd() {
        String devSyncSql = "";
        String sql = "select * from mdp_topic where site = ?";

        List<InlineLink> results = prodJdbcTemplate.query(sql
                , new Object[] { site }
                , new InlineLinksRepository.InlineLinkRowMapper()
        );

        for(InlineLink inlineLink : results) {
            String unique = "select * from mdp_topic where topic_name = ? and topic_url = ? and site =?";
            List<InlineLink> devResults = devJdbcTemplate.query(unique
                 , new Object[] {
                    inlineLink.getTopicName(),
                    inlineLink.getTopicUrl(),
                    inlineLink.getSite()
                 }
                 , new InlineLinksRepository.InlineLinkRowMapper()
            );
            String escapedTopicName = StringUtils.replace(inlineLink.getTopicName(), "'", "''");
            if(devResults.isEmpty()) {
                String insertSql = String.format("insert into mdp_topic(topic_id, topic_name, topic_url, priority, topic_type_id, num_words_in_topic, site) " +
                        "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                        ,inlineLinksRepository.getNextInlineLinkTopicId()
                        ,escapedTopicName
                        ,inlineLink.getTopicUrl()
                        ,inlineLink.getPriority()
                        ,inlineLink.getTopicTypeId()
                        ,inlineLink.getNumWordsInTopic()
                        ,inlineLink.getSite()
                );
                devSyncSql += insertSql + ";";
                devSyncSql += "\n";
            }
        }

//        System.out.println(devSyncSql);
    }

}
