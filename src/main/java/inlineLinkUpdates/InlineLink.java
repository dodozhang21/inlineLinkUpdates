package inlineLinkUpdates;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class InlineLink implements Serializable {
    private int rowNumber;
    private String topicId;

    @NotEmpty(message = "Topic Name may not be empty.")
    private String topicName;

    @NotEmpty(message = "Topic URL may not be empty.")
    private String topicUrl;

    @NotNull(message = "Priority may not be empty, or less than 1.")
    @Min(value = 1)
    private Integer priority;

    private Integer topicTypeId = 2;

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

    public static boolean hasProperty(String property) {
        Field[] fields = InlineLink.class.getDeclaredFields();
        for(Field field : fields) {
            if(field.getName().equals(property)) {
                return true;
            }
        }
        return false;
    }

    public String getStringProperty(String stringPropertyName) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(this.getClass());
        } catch (IntrospectionException e) {
//            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
            if (currentPropertyDescriptor.getName().equals(stringPropertyName)) {
                Method readMethod = currentPropertyDescriptor.getReadMethod();
                try {
                    return (String)readMethod.invoke(this);
                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public static String convertToDbColumn(String propertyName) {
        // convert camelCase to underscore
        return propertyName.replaceAll("([a-z])([A-Z])", "$1_$2");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        InlineLink other = (InlineLink) obj;
        return new EqualsBuilder()
                .append(topicName, other.getTopicName())
                .append(topicUrl, other.getTopicUrl())
                .append(priority, other.getPriority())
                .append(topicTypeId, other.getTopicTypeId())
                .append(numWordsInTopic, other.getNumWordsInTopic())
                .append(site, other.getSite())
                .isEquals();
    }

    public String getUniqueKey() {
        return getTopicName() + "|" + getTopicUrl() + "|" + getSite();
    }

    public String getEscapedTopicNameForDb() {
        return StringUtils.replace(getTopicName(), "'", "''");
    }

    public static int getWordsInTopicName(String topicName) {
        // figure out words in topic name
        String[] words = StringUtils.split(topicName, " ");
        return words.length;
    }
}
