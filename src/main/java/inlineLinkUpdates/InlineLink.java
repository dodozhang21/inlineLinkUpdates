package inlineLinkUpdates;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


public class InlineLink implements Serializable {
    private int rowNumber;
    private String topicId;

    @NotEmpty(message = "Topic Name may not be empty.")
    private String topicName;
    @NotEmpty(message = "Topic URL may not be empty.")
    private String topicUrl;
    @NotNull(message = "Priority may not be empty.")
    @Min(value = 1)
    private Integer priority;
    private Integer topicTypeId;
    private Integer numWordsInTopic;
    @NotEmpty(message = "Site may not be empty.")
    private String site;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getTopicTypeId() {
        return topicTypeId;
    }

    public void setTopicTypeId(Integer topicTypeId) {
        this.topicTypeId = topicTypeId;
    }

    public Integer getNumWordsInTopic() {
        return numWordsInTopic;
    }

    public void setNumWordsInTopic(Integer numWordsInTopic) {
        this.numWordsInTopic = numWordsInTopic;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).toString();
    }
}
