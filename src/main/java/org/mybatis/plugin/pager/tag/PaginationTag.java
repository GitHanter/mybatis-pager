/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.tag;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.mybatis.plugin.pager.model.Page;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年8月26日 下午2:45:14   
 * @version 1.0
 */
public class PaginationTag extends TagSupport {

    /**
     * 
     */
    private static final long serialVersionUID = -6393306536722835208L;

    static String PAGE_NUMBER_PARAM = "pageNumber";

    static String PAGE_SIZE_PARAM = "pageSize";

    static String TURN_TO_PAGE_PARAM = "skipToPage";

    private Page<?> page;

    private String action;

    private String httpMethod = "get";

    public void setPage(Page<?> page) {
        this.page = page;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspTagException {
        if (page == null) {
            throw new JspTagException("The 'page' attribute of the PaginationTag can't be null !");
        }
        if (action == null || action.length() == 0) {
            throw new JspTagException("The 'action' attribute of the PaginationTag can't be empty!");
        }

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        StringBuffer out = new StringBuffer();

        int pageNumber = page.getPageNumber() < 1 ? 1 : page.getPageNumber();
        int pageSize = page.getPageSize() < 0 ? Page.DEFAULT_PAGE_SIZE : page.getPageSize();
        int totalPage = page.getTotalPages() < 0 ? 0 : page.getTotalPages();
        int totalRow = page.getTotalRows() < 0 ? 0 : page.getTotalRows();
        int pagingSize = Page.DEFAULT_PAGING_SIZE;
        int maxPerPage = page.getRecordPerPage();

        out.append("<!------------------------------ start pageTag ------------------------------>\r\n");
        out.append("<nav class=\"navigation paging\" role=\"navigation\">");
        out.append("<form method=\"" + httpMethod + "\" action=\"" + action + "\" name=\"pagerForm\" id=\"pagerForm\"  style=\"padding:0 10px\">");
        out.append("<input type=\"hidden\" id=\"pageNumber\" name=\"pageNumber\" class=\"page-no\" value=\"" + pageNumber + "\"/>");

        Enumeration<?> requestParameters = request.getParameterNames();
        while (requestParameters.hasMoreElements()) {
            Object key = requestParameters.nextElement();
            if (isSystemPageScopeParameter(key.toString())) {
                continue;
            }
            if (!key.equals(PAGE_NUMBER_PARAM) && !key.equals("pageSize") && !key.equals("skipToPage")) {
                out.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + request.getParameter(key.toString()) + "\"/>");
            }
        }

        if (totalPage == 0) {
            out.append(" <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">");
            out.append(" <tbody><tr><td style=\"width:20%\"><div class=\"page-info pull-left\">");
            out.append(" 共" + totalRow + " 条, 每页 ");
            out.append(" <span><input type=\"text\" name=\"pageSize\" id=\"pageSize\" class=\"page-size\" style=\"width: 50px;height: 25px;text-align: center;margin: 0 5px 0 \" value=\""
                      + pageSize + "\" />");
            out.append(" </span>条,共" + totalPage + " 页 ");
            out.append(" </div></td><td style=\"width:65%\"></td><td style=\"width:12%;padding-left:10px\"></td></tr></tbody></table>");
        } else {
            out.append(" <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\"><tbody><tr><td style=\"width:20%\"><div class=\"page-info pull-left\">");
            out.append(" 共" + totalRow + " 条, 每页 ");
            out.append(" <span><input type=\"text\" name=\"pageSize\" id=\"pageSize\" class=\"page-size\" style=\"width: 50px;height: 25px;text-align: center;margin: 0 5px 0 \" value=\""
                      + pageSize + "\" />");
            out.append("</span>条,共" + totalPage + " 页 ");
            out.append(" </div></td><td style=\"width:65%\"><ul class=\"pagination pull-right no-margin\">");

            // 首页
            if (totalPage > pagingSize) {
                if (pageNumber == 1) {
                    out.append(" <li class=\"first disabled\"><a href=\"javascript:void(0)\">首页</a></li> ");
                } else {
                    out.append(" <li class=\"first\" data-page-no=\"1\"><a href=\"javascript:$.turnOverPage(1)\">首页</a></li> ");
                }
            }

            // 上一页
            if (pageNumber == 1) {
                out.append("<li class=\"previous disabled\"><a href=\"javascript:void(0)\">上一页</a></li> ");
            } else {
                out.append("<li class=\"previous\" data-page-no=\"" + (pageNumber - 1) + "\"><a href=\"javascript:$.turnOverPage(" + (pageNumber - 1)
                            + ")\">上一页</a></li> ");
            }

            int startPage = 0, endPage = 0;
            // 滑动窗口,滑动窗口大小为pagingSize
            if (totalPage > pagingSize) {
                startPage = pageNumber - (pagingSize / 2);
                startPage = startPage < 1 ? 1 : startPage;

                endPage = startPage + pagingSize - 1;
                if (endPage > totalPage) {
                    endPage = totalPage;
                    startPage = totalPage - pagingSize + 1;
                }
            } else {
                startPage = 1;
                endPage = totalPage;
            }

            // ...
            if (totalPage > pagingSize && startPage != 1) {
                out.append("<li class=\"extend disabled\"><a href=\"javascript:void(0)\">...</a></li>");
            }

            for (int i = startPage; i <= endPage; i++) {
                if (pageNumber == i) {
                    // 当前页
                    out.append(" <li class=\"current active\"><a href=\"javascript:void(0)\">" + i + "</a></li> ");
                } else {
                    out.append("<li class=\"\" data-page-no=\"" + i + "\"><a href=\"javascript:$.turnOverPage(" + i + ")\">" + i + "</a></li>");
                }
            }

            // ...
            if (totalPage > pagingSize && endPage != totalPage) {
                out.append("<li class=\"extend disabled\"><a href=\"javascript:void(0)\">...</a></li>");
            }

            // 下一页
            if (pageNumber == totalPage) {
                out.append(" <li class=\"next disabled\"><a href=\"javascript:void(0)\">下一页</a></li> ");
            } else {
                out.append(" <li class=\"next\" data-page-no=\"" + (pageNumber + 1) + "\"><a href=\"javascript:$.turnOverPage(" + (pageNumber + 1)
                            + ")\">下一页</a></li> ");
            }

            // 尾页
            if (totalPage > pagingSize) {
                if (pageNumber == totalPage) {
                    out.append(" <li class=\"last disabled\"><a href=\"javascript:void(0)\">尾页</a></li> ");
                } else {
                    out.append(" <li class=\"last\" data-page-no=\"" + totalPage + "\"><a href=\"javascript:$.turnOverPage(" + totalPage + ")\">尾页</a></li> ");
                }
            }

            out.append(" </ul></td><td style=\"width:12%;padding-left:10px\"><div class=\"page-info pull-left\">跳至<span>"
                        + "<input type=\"text\" id=\"skipToPage\" name=\"skipToPage\" class=\"page-size\" style=\"width: 50px;height: 25px;text-align: center;margin: 0 5px 0 \" value=\"\"/>"
                        + "</span>页</div></td></tr></tbody></table> ");

        }

        out.append(" </ul></form> ");

        out.append(" <script type=\"text/javascript\"> \r\n");
        out.append(" $(function () { \r\n");
                   out.append("            $pForm = $(\"#pagerForm\"); \r\n");
                   out.append("            $pageNumber = $(\"#pageNumber\"); \r\n");
                   out.append("            $pageSize = $(\"#pageSize\"); \r\n");
                   out.append("            $skipToPage = $(\"#skipToPage\"); \r\n");
                   out.append("            $.turnOverPage = function (pageNumber) { \r\n");
        out.append("                if (pageNumber >" + totalPage + ") { \r\n");
        out.append("                    pageNumber =" + totalPage + "; \r\n");
                   out.append("                } \r\n");
                   out.append("                if (pageNumber < 1) { \r\n");
                   out.append("                    pageNumber = 1; \r\n");
                   out.append("                } \r\n");
                   out.append("                $pageNumber.val(pageNumber);\r\n");
                   out.append("                $pForm.submit(); \r\n");
                   out.append("                return false; \r\n");
                   out.append("            }; \r\n");
                   out.append("            \r\n");
                   out.append("            //改变每页大小 pageSize \r\n");
                   out.append("            $pageSize.keydown(function (e) { \r\n");
                   out.append("                var keynum; \r\n");
                   out.append("                if (window.event) // IE \r\n");
                   out.append("                { \r\n");
                   out.append("                    keynum = e.keyCode;\r\n");
                   out.append("                } \r\n");
                   out.append("                else if (e.which) // Netscape/Firefox/Opera \r\n");
                   out.append("                { \r\n");
                   out.append("                    keynum = e.which; \r\n");
                   out.append("                } \r\n");
                   out.append("                if (keynum == 13) { \r\n");
                   out.append("                    var per = $pageSize.val(); \r\n");
        out.append("                    if (per < 1 || per > " + maxPerPage + ") { \r\n");
                   out.append("                        return false; \r\n");
                   out.append("                    } else { \r\n");
                   out.append("                        $pageSize.val(per); \r\n");
                   out.append("                        $pageNumber.val(1); \r\n");
                   out.append("                        $pForm.submit(); \r\n");
                   out.append("                    } \r\n");
                   out.append("                } \r\n");
                   out.append("                return true; \r\n");
                   out.append("            }); \r\n");
                   out.append("            //跳转到某页的功能 \r\n");
                   out.append("            $skipToPage.keydown(function (e) { \r\n");
                   out.append("                var keynum; \r\n");
                   out.append("                if (window.event) // IE \r\n");
                   out.append("                { \r\n");
                   out.append("                    keynum = e.keyCode; \r\n");
                   out.append("                } \r\n");
                   out.append("                else if (e.which) // Netscape/Firefox/Opera \r\n");
                   out.append("                { \r\n");
                   out.append("                    keynum = e.which; \r\n");
                   out.append("                } \r\n");
                   out.append("                if (keynum == 13) { \r\n");
                   out.append("                    var toPage = $skipToPage.val(); \r\n");
        out.append("                    if (toPage < 1 || toPage >" + totalPage + ") { \r\n");
                   out.append("                        return false; \r\n");
                   out.append("                    } else { \r\n");
                   out.append("                        $pageNumber.val(toPage); \r\n");
                   out.append("                        $pForm.submit(); }} \r\n");
                   out.append("                return true; }); }); \r\n");
                   out.append("    </script> \r\n");
        out.append(" </nav> ");
        out.append("<!------------------------------ end pageTag ------------------------------>\r\n");
        try {
            pageContext.getOut().write(out.toString());
        } catch (IOException e) {

        }
        return EVAL_PAGE;
    }

    private boolean isSystemPageScopeParameter(String key) {
        if (key == null || key.length() == 0) {
            return true;
        }
        return key.startsWith("javax.servlet") || key.startsWith("__spring") || key.startsWith("org.") || key.contains("FILTERED");
    }
}