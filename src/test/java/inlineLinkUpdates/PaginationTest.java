package inlineLinkUpdates;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PaginationTest {
    @Test
    public void testPagination_perfectDivision() {
        Pagination pagination = new Pagination();

        pagination.setTotalResults(100);
        pagination.setNumberPerPage(25);
        pagination.setCurrentPage(3);

        assertEquals(4, pagination.getTotalPages());
        assertEquals(51, pagination.getStartRow());
        assertEquals(75, pagination.getEndRow());
    }


    @Test
    public void testPagination_perfectDivisionPlusOneMore() {
        Pagination pagination = new Pagination();

        pagination.setTotalResults(101);
        pagination.setNumberPerPage(25);
        pagination.setCurrentPage(5);

        assertEquals(5, pagination.getTotalPages());
        assertEquals(101, pagination.getStartRow());
        assertEquals(101, pagination.getEndRow());
    }
}
