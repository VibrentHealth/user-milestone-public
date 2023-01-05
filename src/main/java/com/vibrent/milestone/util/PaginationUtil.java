package com.vibrent.milestone.util;

import com.vibrent.usermilestone.dto.Link;
import com.vibrent.usermilestone.dto.Page;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaginationUtil {

    private PaginationUtil() {
    }

    private static final String SIZE = "&size=";
    private static final String PAGE = "&page=";

    public static Page getPageInfo(int totalPages, long countResult, int pageSize, int pageNo) {
        Page page = new Page();
        page.setNumber(pageNo);
        page.setSize(pageSize);
        page.setTotalPages(totalPages);
        page.setTotalElements(countResult);
        return page;
    }

    public static List<Link> addLinks(int totalPages, String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until, int size, int page) {
        HttpServletRequest servletRequest = HttpRequestUtils.getCurrentRequest();
        StringBuilder requestUrl = new StringBuilder(servletRequest.getRequestURL().toString());
        List<Link> links = new ArrayList<>();
        links.add(getlink(totalPages, consumer, latest, since, until, size, page, "first", requestUrl));
        links.add(getlink(totalPages, consumer, latest, since, until, size, page, "prev", requestUrl));
        links.add(getlink(totalPages, consumer, latest, since, until, size, page, "self", requestUrl));
        links.add(getlink(totalPages, consumer, latest, since, until, size, page, "next", requestUrl));
        links.add(getlink(totalPages, consumer, latest, since, until, size, page, "last", requestUrl));
        return links;
    }

    private static Link getlink(int totalPages, String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until, int size, int page, String type, StringBuilder requestUrl){
        Link link = new Link();
        link.setRel(type);
        String request = getHref(consumer, latest, since, until);
        String requestPageSize = getPageAndSize(totalPages,size,page,type);
        link.setHref(requestUrl+request+requestPageSize);
        return link;
    }
    private static String getHref(String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until){
        String rel = "";
        if(Objects.nonNull(consumer)){
            rel=rel+"?consumer="+consumer;
        }
        if(latest.isPresent()){
            rel=rel+"&latest="+latest.get();
        }
        if(since.isPresent()){
            rel=rel+"&since="+since.get();
        }
        if(until.isPresent()){
            rel=rel+"&until="+until.get();
        }
        return rel;
    }

    private static String getPageAndSize(int totalPages, int size, int page, String type) {
        String rel = "";
        if (type.equals("self")) {
            rel = rel + SIZE + size + PAGE + page;
        }
        if (type.equals("first")) {
            rel = rel + SIZE + size + PAGE + 0;
        }
        if (type.equals("last")) {
            rel = rel + SIZE + size + PAGE + (totalPages> 0 ? (totalPages -1) : 0);
        }
        if (type.equals("next")) {
            rel = rel + SIZE + size + PAGE + (page < (totalPages - 1) ? page + 1 : page);
        }
        if (type.equals("prev")) {
            rel = rel + SIZE + size + PAGE + (page > 0 ? page - 1 : page);
        }

        return rel;
    }


}
