package utils;

import au.com.bytecode.opencsv.CSVReader;
import inlineLinkUpdates.InlineLink;
import inlineLinkUpdates.InlineLinksRepository;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SyncMdpTopicContext.class, InlineLinksRepository.class})
public class DevUpdateFromCsv {
    // it's currently expecting TOPIC_ID	SITE	TOPIC_NAME	TOPIC_URL
    // but update transformCsvToObject if the csv changes

    private final String pathToUpdateCsv = "C:/tmp/updatedInlineLinks.csv";
    private final String pathToDeleteCsv = "C:/tmp/deletedInlineLinks.csv";

    @Autowired
    @Qualifier("prodJdbcTemplate")
    private JdbcTemplate prodJdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate devJdbcTemplate;

    @Autowired
    private InlineLinksRepository inlineLinksRepository;

    private InlineLink transformCsvToObject(String[] line) {
        InlineLink inlineLink = new InlineLink();
        inlineLink.setTopicId(StringUtils.trim(line[0]));
        inlineLink.setSite(StringUtils.trim(line[1]));
        inlineLink.setTopicName(StringUtils.trim(line[2]));
        inlineLink.setTopicUrl(StringUtils.trim(line[3]));
        return inlineLink;
    }

    @Test
    public void generateSql() throws IOException {
        String sqlForProd = "";
        String unique = "select * from mdp_topic where topic_name = ? and topic_url = ? and site = ?";

        CSVReader reader = new CSVReader(new FileReader(pathToUpdateCsv));
        List<String[]> updatedInlineLinks = reader.readAll();
        for(String[] entry : updatedInlineLinks){
            // transform to obj
            InlineLink inlineLink = transformCsvToObject(entry);

            // go ahead check if the record exists no matter what
            List<InlineLink> inProd = prodJdbcTemplate.query(unique, new Object[] {
                    inlineLink.getTopicName()
                    ,inlineLink.getTopicUrl()
                    ,inlineLink.getSite()
            }, new InlineLinksRepository.InlineLinkRowMapper());


            // if the record doesn't exist in prod
            if(inProd.isEmpty()) {
                // also check if the unique topic id exists
                InlineLink existing = inlineLinksRepository.findInlineLinkByTopicId(inlineLink.getTopicId());
                String insertSql = "";
                if(existing == null) {
                    insertSql = String.format("insert into mdp_topic(topic_id, topic_name, topic_url, priority, topic_type_id, num_words_in_topic, site) " +
                                    "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                            ,inlineLinksRepository.getNextInlineLinkTopicId()
                            ,inlineLink.getEscapedTopicNameForDb()
                            ,inlineLink.getTopicUrl()
                            ,20
                            ,2
                            ,InlineLink.getWordsInTopicName(inlineLink.getTopicName())
                            ,inlineLink.getSite()
                    );

                } else {
                    insertSql = String.format("insert into mdp_topic(topic_id, topic_name, topic_url, priority, topic_type_id, num_words_in_topic, site) " +
                                    "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                            ,existing.getTopicId()
                            ,inlineLink.getEscapedTopicNameForDb()
                            ,inlineLink.getTopicUrl()
                            ,existing.getPriority()
                            ,existing.getTopicTypeId()
                            ,InlineLink.getWordsInTopicName(inlineLink.getTopicName())
                            ,inlineLink.getSite()
                    );
                }

                sqlForProd += insertSql + ";";
                sqlForProd += "\n";

            }

        } // end of updated

        CSVReader deleteReader = new CSVReader(new FileReader(pathToDeleteCsv));
        List<String[]> deletedInlineLinks = deleteReader.readAll();

        for(String[] entry : deletedInlineLinks){
            InlineLink inlineLink = transformCsvToObject(entry);

            String deleteContentTopic = String.format("delete from mdp_content_topic where topic_id in (select topic_id from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s')"
                    ,inlineLink.getEscapedTopicNameForDb()
                    ,inlineLink.getTopicUrl()
                    ,inlineLink.getSite()
            );
            String deleteTopic = String.format("delete from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s'"
                    , inlineLink.getEscapedTopicNameForDb()
                    , inlineLink.getTopicUrl()
                    , inlineLink.getSite()
            );
            sqlForProd += deleteContentTopic + ";";
            sqlForProd += "\n";
            sqlForProd += deleteTopic + ";";
            sqlForProd += "\n";
        }

        System.out.println(sqlForProd);
    }

    @Test
    public void inDevButNotInSpreadSheet() throws IOException {
        String site = "parents";

        System.out.println(inDevButNotInSpreadSheet(site));
    }

    private String inDevButNotInSpreadSheet(String site) throws IOException {
        String sql = "select * from mdp_topic where site = ?";

        // unique key = topicName|topicUrl|site
        Map<String, InlineLink> uniqueMap = new HashMap<String, InlineLink>();

        CSVReader reader = new CSVReader(new FileReader(pathToUpdateCsv));
        List<String[]> updatedInlineLinks = reader.readAll();
        for(String[] entry : updatedInlineLinks) {
            InlineLink inlineLink = transformCsvToObject(entry);

            uniqueMap.put(inlineLink.getUniqueKey(), inlineLink);
        }


        List<InlineLink> results = devJdbcTemplate.query(sql, new Object[] {site}, new InlineLinksRepository.InlineLinkRowMapper());

        String sqlForDev = "";
        for(InlineLink result : results) {
            if(uniqueMap.get(result.getUniqueKey()) == null) {
                String deleteContentTopic = String.format("delete from mdp_content_topic where topic_id in (select topic_id from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s')"
                        ,result.getEscapedTopicNameForDb()
                        ,result.getTopicUrl()
                        ,result.getSite()
                );
                String deleteTopic = String.format("delete from mdp_topic where topic_name = '%s' and topic_url = '%s' and site = '%s'"
                        , result.getEscapedTopicNameForDb()
                        , result.getTopicUrl()
                        , result.getSite()
                );
                sqlForDev += deleteContentTopic + ";";
                sqlForDev += "\n";
                sqlForDev += deleteTopic + ";";
                sqlForDev += "\n";
            }
        }

        return sqlForDev;
    }


}
