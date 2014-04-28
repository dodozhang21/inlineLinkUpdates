package inlineLinkUpdates;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class InlineLinksRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getDistinctSites() {
        String sql = "select distinct site from mdp_topic order by site desc";

        return jdbcTemplate.queryForList(sql, String.class);
    }

    public String getNextInlineLinkTopicId() {
        return jdbcTemplate.queryForObject("select concat('app',to_char(inlineLinkUpdates_seq.nextval)) from dual", String.class);
    }

    public void addInlineLink(InlineLink inlineLink) {
        String sql = "insert into mdp_topic(topic_id, topic_name, topic_url, priority, topic_type_id, num_words_in_topic, site) values (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, new Object[] {
                inlineLink.getTopicId()
                ,inlineLink.getTopicName()
                ,inlineLink.getTopicUrl()
                ,inlineLink.getPriority()
                ,inlineLink.getTopicTypeId()
                ,inlineLink.getNumWordsInTopic()
                ,inlineLink.getSite()
        });
    }

    public void deleteInlineLink(String topicId) {
        // delete from join table
        String joinSql = "delete from mdp_content_topic where topic_id = ?";
        String sql = "delete from mdp_topic where topic_id = ?";

        jdbcTemplate.update(joinSql, new Object[] {topicId});
        jdbcTemplate.update(sql, new Object[] {topicId});
    }

    public List<InlineLink> searchInlineLinks(Map<String, String> parameters) {
        return searchInlineLinks(parameters, null, null);
    }

    public List<InlineLink> searchInlineLinks(Map<String, String> parameters, String orderBy) {
        return searchInlineLinks(parameters, orderBy, null);
    }

    public List<InlineLink> searchInlineLinks(Map<String, String> parameters, Pagination pagination) {
        return searchInlineLinks(parameters, null, pagination);
    }

    public List<InlineLink> searchInlineLinks(Map<String, String> parameters, String orderBy, Pagination pagination) {
        String sql = buildSql("select * from mdp_topic", parameters, orderBy);

        // values for search
        Object[] values = new Object[parameters.size()];
        int i = 0;
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            values[i] = entry.getValue();
            i++;
        }

        List<InlineLink> results = new ArrayList<InlineLink>();

        // pagination total results
        if(pagination != null) {
            String countSql = buildSql("select count(topic_id) from mdp_topic", parameters, orderBy);
            int totalResults = jdbcTemplate.queryForObject(countSql, values, Integer.class);
            pagination.setTotalResults(totalResults);

            // add pagination on query
            sql = buildPaginationSql(sql, pagination);

            results = jdbcTemplate.query(
                    sql
                    ,values
                    ,new InlineLinkPaginationRowMapper()
            );

        } else {
            results = jdbcTemplate.query(
                    sql
                    ,values
                    ,new InlineLinkRowMapper()
                );
        }

        return results;
    }

    public InlineLink findInlineLinkByTopicId(String topicId) {
        String sql = "select * from mdp_topic where topic_id = ?";

        List<InlineLink> results = jdbcTemplate.query(sql
                                    , new Object[] {
                                        topicId
                                    }
                                    , new InlineLinkRowMapper()
                                    );
        return results.isEmpty() ? null : results.get(0);
    }

    public List<InlineLink> findLikeRows(String parameter, String value, InlineLink like) {
        String dbColumn = InlineLink.convertToDbColumn(parameter);
        String sql = String.format("select * from (select * from mdp_topic where upper(%s) like upper('%%%s%%') and site = ? order by %s) where ROWNUM <= 20"
                , dbColumn
                , value
                , dbColumn);
        return jdbcTemplate.query(sql, new Object[] {
                    like.getSite()
                }
                , new InlineLinkRowMapper());
    }

    public void updateInlineLink(InlineLink inlineLink) {

        String sql = "update mdp_topic set topic_name = ?, topic_url = ?, priority = ?, num_words_in_topic = ? where topic_id = ?";

        jdbcTemplate.update(sql, new Object[] {
                inlineLink.getTopicName(),
                inlineLink.getTopicUrl(),
                inlineLink.getPriority(),
                inlineLink.getNumWordsInTopic(),
                inlineLink.getTopicId()
                });
    }

    public static class InlineLinkRowMapper implements RowMapper<InlineLink> {
        @Override
        public InlineLink mapRow(ResultSet rs, int rowNum) throws SQLException {
            InlineLink inlineLink = new InlineLink();
            inlineLink.setTopicId(rs.getString("topic_id"));
            inlineLink.setTopicName(rs.getString("topic_name"));
            inlineLink.setTopicUrl(rs.getString("topic_url"));
            inlineLink.setPriority(rs.getInt("priority"));
            inlineLink.setTopicTypeId(rs.getInt("topic_type_id"));
            inlineLink.setNumWordsInTopic(rs.getInt("num_words_in_topic"));
            inlineLink.setSite(rs.getString("site"));
            return inlineLink;
        }
    }

    public static class InlineLinkPaginationRowMapper extends InlineLinkRowMapper {
        @Override
        public InlineLink mapRow(ResultSet rs, int rowNum) throws SQLException {
            InlineLink inlineLink = super.mapRow(rs, rowNum);
            inlineLink.setRowNumber(rs.getInt("rnum"));
            return inlineLink;
        }
    }


    public static String buildSql(String sqlSelect, Map<String, String> parameters, String orderBy) {
        String sql = sqlSelect;
        if(!parameters.isEmpty()) {
            List<String> whereClauses = new ArrayList<String>();
            for(String key : parameters.keySet()) {
                if("topic_name".equals(key) || "topic_url".equals(key)){
                    whereClauses.add(String.format("upper(%s) like upper(?)", key));
                }
                else {
                    whereClauses.add(String.format("upper(%s) = upper(?)", key));
                }
            }
            sql += " where " + StringUtils.join(whereClauses, " and ");
        }
        if(StringUtils.isBlank(orderBy)) {
            sql += " order by topic_name asc";
        } else {
            sql += " order by " + orderBy;
        }
        return sql;
    }

    public static String buildPaginationSql(String sql, Pagination pagination) {
        String paginationSql = String.format("SELECT * FROM ( SELECT rownum rnum, a.* from ( %s ) a ) WHERE rnum BETWEEN %s AND %s",
                sql
                ,pagination.getStartRow()
                ,pagination.getEndRow()
        );

        return paginationSql;
    }

}

