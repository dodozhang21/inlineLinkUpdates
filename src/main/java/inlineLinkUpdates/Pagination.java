package inlineLinkUpdates;

import java.io.Serializable;

public class Pagination implements Serializable {
    private int totalResults;
    private int numberPerPage = 25;
    private int currentPage = 1;


    public int getStartRow() {
        return (currentPage - 1) * numberPerPage + 1;
    }

    public int getEndRow() {
        int endRow = getStartRow() + numberPerPage - 1;
        if(endRow >= totalResults) {
            return totalResults;
        } else {
            return endRow;
        }
    }

    public int getTotalPages() {
        return ((Double)Math.ceil(Double.valueOf(totalResults) / Double.valueOf(numberPerPage))).intValue();
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getNumberPerPage() {
        return numberPerPage;
    }

    public void setNumberPerPage(int numberPerPage) {
        this.numberPerPage = numberPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
