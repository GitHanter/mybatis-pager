<#--
* Freemarker 分页插件，使用Boostrap Panitaion样式，当然也可以自己定义样式。使用时引入boostrap.min.css和 jquery
* Freemarker 作为Spring MVC的一类view，FreeMarkerConfigurer需配置以下属性 
*	<bean id="freeMarkerConfigurer" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
*		<property name="preferFileSystemAccess" value="false" />
*		<property name="templateLoaderPaths" value="/path/to/your/templates,classpath:/freemarker/" />
*		<property name="freemarkerVariables">
*			<map>
*				<entry key="base" value="#{servletContext.contextPath}" />
*			</map>
*		</property>
*		...
*	</bean>
* Demo:
* (1) list.ftl
* <head>
*	<meta http-equiv="content-type" content="text/html; charset=utf-8" /> 
* </head>
* <body>
*	<div id="search">
*		<form target="listForm" name="searchForm" id="searchForm" action="${base}/app/users/list" method="get">
*			<table border="1" width="650px" align="center">
*			  <tr>
*			  	<td align="right" width="150px">根据名称模糊搜索:</td>
*			  	<td><input type="text" name="firstName" value="${RequestParameters.firstName!''}"/>&nbsp;
*			  	    <input type="submit" value=" 搜索 "/></td></tr>
*			</table>
*		</form>
*  	</div>
*	<div style="height:1000px;width:200px">
*		<iframe src="${base}/app/users/list" name="listForm" id="listForm" frameborder="0" scrolling="no" style="height:1000px;width:1900px" />
*	</div>
* </body>
* 
* (2) list_table.ftl
*	<head>
*		<meta http-equiv="content-type" content="text/html; charset=utf-8" /> 
*		<link href="${base}/resources/lib/Bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
*		<script type="text/javascript" src="${base}/resources/lib/jQuery/jquery-1.11.2.min.js"></script>
*   </head>
*	<body>
*		<div>
*			<#import "pagination.ftl" as pager/> 
*			<@pager.pagination page "${base}/app/users/list"/>
*		</div>
*	
*		<div style="height:800px;width:800px;margin-left:150px">
*			<table id="listTable" class="list">
*				<tr>
*					<th>id</th>
*					<th>名字</th>
*					<th>姓</th>
*				</tr>
*				<#if (page.content)??>
*					<#list page.content as user>
*						<tr>
*							<td>${user.id}</td>
*							<td>${user.firstName}</td>
*							<td>${user.lastName}</td>
*						</tr>
*					</#list>
*				</#if>
*			</table>
*		</div>
*		
*	</body>
-->
<#macro pagination page url="">
    <#assign pageNumber=(page.pageNumber)!1 pageSize=(page.pageSize) totalPage=(page.totalPages)!0 totalRow=(page.totalRows)!0 maxPerPage=(page.recordPerPage) >
    <#if (pageNumber > totalPage)>
        <#assign pageNumber=totalPage>
    </#if>

    <#if (pageNumber < 1) >
        <#assign pageNumber=1>
    </#if>

<#--显示多少个页码-->
    <#assign pagingSize = 5>

<nav class="navigation paging" role="navigation">
    <form method="post" action="${url}" name="pagerForm" id="pagerForm"  style="padding:0 10px">

        <input type="hidden" id="pageNumber" name="pageNumber" class="page-no" value="${pageNumber}"/>

    <#--List查询参数-->
        <#list RequestParameters?keys as key>
            <#if (key!="pageNumber" && key!="pageSize" && key!="skipToPage" && RequestParameters[key]??)>
                <input type="hidden" name="${key}" value="${RequestParameters[key]}"/>
            </#if>
        </#list>

            <#if totalPage == 0>
            <table width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                <tbody>
                    <tr>
                        <td style="width:20%">
                            <div class="page-info pull-left">
                            		共${totalRow}条, 每页
                            	<span>
	                            <input type="text" name="pageSize" id="pageSize" class="page-size" 
	                                    style="width: 50px;height: 25px;text-align: center;margin: 0 5px 0 "
	                                    value="${pageSize}" />
                                </span>条,共${totalPage}页
                            </div>
                        </td>
                        <td style="width:65%">
                        </td>
                        <td style="width:12%;padding-left:10px">
                        </td>
                    </tr>
                </tbody>
            </table>
            <#else>
                <table width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
                    <tbody>
                    <tr>
                        <td style="width:20%">
                            <div class="page-info pull-left">
                            			共${totalRow}条, 每页
                                <span>
                                	<input type="text" name="pageSize" id="pageSize" class="page-size" 
                                	       style="width: 50px;height: 25px;text-align: center;margin: 0 5px 0 "
                                	       value="${pageSize}" />
                               </span>条, 共${totalPage}页
                            </div>
                        </td>
                        <td style="width:65%">
                            <ul class="pagination pull-right no-margin">

                                <!-- 首页 -->
                                <#if (totalPage > pagingSize)>
                                    <#if pageNumber == 1>
                                        <li class="first disabled"><a href="javascript:void(0)">首页</a></li>
                                    <#else>
                                        <li class="first" data-page-no="1"><a href="javascript:$.turnOverPage(1)">首页</a></li>
                                    </#if>
                                </#if>

                                <!-- 上一页 -->
                                <#if pageNumber == 1>
                                    <li class="previous disabled"><a href="javascript:void(0)">上一页</a></li>
                                <#else>
                                    <li class="previous" data-page-no="${pageNumber-1}"><a href="javascript:$.turnOverPage(${pageNumber-1})">上一页</a></li>
                                </#if>

                                <!-- 滑动窗口,滑动窗口大小为pagingSize -->
                                <#if (totalPage > pagingSize)>
                                    <#assign startPage = pageNumber - (pagingSize / 2) ? floor>
                                    <#if (startPage < 1)>
                                        <#assign startPage = 1>
                                    </#if>

                                    <#assign endPage = startPage + pagingSize - 1>

                                    <#if (endPage > totalPage)>
                                        <#assign endPage = totalPage startPage = totalPage - pagingSize + 1>
                                    </#if>
                                <#else>
                                    <#assign startPage = 1 endPage = totalPage>
                                </#if>

                                <!-- ... -->
                                <#if (totalPage > pagingSize && startPage != 1)>
                                    <li class="extend disabled"><a href="javascript:void(0)">...</a></li>
                                </#if>

                                <#list startPage..endPage as i>
                                    <#if pageNumber == i>
                                        <!-- 当前页 -->
                                        <li class="current active"><a href="javascript:void(0)">${i}</a></li>
                                    <#else>
                                        <li class="" data-page-no="${i}"><a href="javascript:$.turnOverPage(${i})">${i}</a></li>
                                    </#if>

                                </#list>

                                <!-- ... -->
                                <#if (totalPage > pagingSize && endPage != totalPage)>
                                    <li class="extend disabled"><a href="javascript:void(0)">...</a></li>
                                </#if>

                                <!-- 下一页 -->
                                <#if pageNumber == totalPage>
                                    <li class="next disabled"><a href="javascript:void(0)">下一页</a></li>
                                <#else>
                                    <li class="next" data-page-no="${pageNumber+1}"><a
                                            href="javascript:$.turnOverPage(${pageNumber+1})">下一页</a>
                                    </li>
                                </#if>

                                <!-- 尾页 -->
                                <#if (totalPage > pagingSize)>
                                    <#if pageNumber == totalPage>
                                        <li class="last disabled"><a href="javascript:void(0)">尾页</a></li>
                                    <#else>
                                        <li class="last" data-page-no="${totalPage}"><a
                                                href="javascript:$.turnOverPage(${totalPage})">尾页</a></li>
                                    </#if>
                                </#if>
                            </ul>
                        </td>
                        <td style="width:12%;padding-left:10px">
                            <div class="page-info pull-left">
                                                                                                  跳至<span><input type="text" id="skipToPage" name="skipToPage" class="page-size" style="width: 50px;height: 25px;text-align: center;margin: 0 5px 0 " value=""/></span>页
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </#if>
        </ul>
    </form>
    <script type="text/javascript">
        $(function () {
            $pForm = $("#pagerForm");
            $pageNumber = $("#pageNumber");
            $pageSize = $("#pageSize");
            $skipToPage = $("#skipToPage");

            $.turnOverPage = function (pageNumber) {
                if (pageNumber >${totalPage}) {
                    pageNumber =${totalPage};
                }
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                $pageNumber.val(pageNumber);
                $pForm.submit();
                return false;
            };
			
            //改变每页大小 pageSize
            $pageSize.keydown(function (e) {
                var keynum;

                if (window.event) // IE
                {
                    keynum = e.keyCode;
                }
                else if (e.which) // Netscape/Firefox/Opera
                {
                    keynum = e.which;
                }
                if (keynum == 13) {
                    var per = $pageSize.val();

                    if (per < 1 || per > ${maxPerPage}) {
                        return false;
                    } else {
                        $pageSize.val(per);
                        $pageNumber.val(1);
                        $pForm.submit();
                    }
                }
                return true;
            });

            //跳转到某页的功能
            $skipToPage.keydown(function (e) {
                var keynum;

                if (window.event) // IE
                {
                    keynum = e.keyCode;
                }
                else if (e.which) // Netscape/Firefox/Opera
                {
                    keynum = e.which;
                }
                if (keynum == 13) {
                    var toPage = $skipToPage.val();

                    if (toPage < 1 || toPage >${totalPage}) {
                        return false;
                    } else {
                        $pageNumber.val(toPage);
                        $pForm.submit();
                    }
                }
                return true;
            });

        });

    </script>
</nav>
</#macro>