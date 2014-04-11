package inlineLinkUpdates;


import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class InlineLinkTest {
    @Test
    public void testConvertToDbColumn() {
        assertEquals("topic_name", InlineLink.convertToDbColumn("topicName").toLowerCase());
        assertEquals("topic_url", InlineLink.convertToDbColumn("topicUrl").toLowerCase());
        assertEquals("topic_id", InlineLink.convertToDbColumn("topicId").toLowerCase());

    }

    @Test
    public void testGetProperty() throws IntrospectionException, InvocationTargetException, IllegalAccessException {

        InlineLink bean = new InlineLink();
        bean.setTopicName("blah");
        bean.setTopicId("hello");
        bean.setTopicUrl("kitty");

        assertEquals("blah", bean.getStringProperty("topicName"));
        assertEquals("hello", bean.getStringProperty("topicId"));
        assertEquals("kitty", bean.getStringProperty("topicUrl"));
    }

    private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass,
                                                     String propertyname) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor propertyDescriptor = null;
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
            if (currentPropertyDescriptor.getName().equals(propertyname)) {
                propertyDescriptor = currentPropertyDescriptor;
            }

        }
        return propertyDescriptor;
    }
}
