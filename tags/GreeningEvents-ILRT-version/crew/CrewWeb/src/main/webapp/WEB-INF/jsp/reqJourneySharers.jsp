<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><fmt:message key="journeysharers.find.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
<c:if test="${not empty event}">
    <meta name="caboto-annotation" content="${event.id}"/>
</c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
</head>
<body>
    <div id="doc2" class="yui-t2">
        <div id="hd">
            <div id="header">
                <a href="http://economicsnetwork.ac.uk/" name="top" id="top" accesskey="1">
                        <img src="http://economicsnetwork.ac.uk/nav/acadlogo.gif" id="logo" alt="Economics Network of the Higher Education Academy" title="Home Page of the Economics Network" style="width:292px;height:151px;border:0" />
                </a>
                <ul id="navlist">
                    <li class="toabout">
                            <a href="http://economicsnetwork.ac.uk/about" accesskey="2">About Us</a>
                    </li>
                    <li class="topubs">
                            <a href="http://economicsnetwork.ac.uk/journals" accesskey="3">Lecturer Resources</a>
                    </li>
                    <li class="tores">
                            <a href="http://economicsnetwork.ac.uk/resources" accesskey="4">Learning Materials</a>
                    </li>
                    <li class="tofunds">
                            <a href="http://economicsnetwork.ac.uk/projects" accesskey="5">Projects&nbsp;&amp; Funding</a>
                    </li>
                    <li class="tonews">
                            <a href="http://economicsnetwork.ac.uk/news" accesskey="6">News&nbsp;&amp; Events</a>
                    </li>
                    <li class="tothemes">
                            <a href="http://economicsnetwork.ac.uk/subjects/" accesskey="7">Browse by Topic</a>
                    </li>
                    <li id="help">
                            <a href="http://economicsnetwork.ac.uk/tenways" style="color:#036" accesskey="?">Help</a>
                    </li>
                </ul>
            </div>
            <div id="homelink">
                    <a href="http://economicsnetwork.ac.uk/">Home</a>
            </div>
        </div>
        <div id="bd">
            <div id="yui-main">
                <div class="yui-b">
                    <div class="yui-gc">
                        <div class="yui-u first" id="content" style="width: 98%"> <!-- width: 66%; float: left -->
                            <div id="content-header">
                                <h1 class="title"><fmt:message key="journeysharers.find.title"/></h1> <%-- Mid column main page title --%>
                                  <div class="breadcrumb"><a href="${pageContext.request.contextPath}/">Greening Events Home</a></div>


                                  </div> <!-- /#content-header -->

    <div id="content-area" style="margin-top: 2em">

           <form:form action="./reqJourneySharers.do" commandName="journeySharersForm">
                <table class="list-table">
                    <thead class="list-header">
                        <tr>
                            <td>&nbsp;</td>
                            <td><fmt:message key="list.users.name"/></td>
                            <td><fmt:message key="list.users.distance"/></td>
                        </tr>
                    </thead>
                    <tbody class="list-body">
                        <c:forEach var="person" items="${journeysharers}" varStatus="status">
                            <c:choose>
                                <c:when test="${status.count % 2 == 1}"><tr></c:when>
                                <c:otherwise><tr class="alt-row"></c:otherwise>
                            </c:choose>
                            <td><form:checkbox path="usernames"
                                                  value="${person.username}"/></td>
                            <td>${person.name}</td>
                            <td>${distances[person.username]}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div id="journeyReqContent">
                    <p>
                        <fmt:message key="journeysharer.message.emailmessage"/>
                    </p>
                    <p>
                        <form:textarea path="message" rows="5" cols="60"/>
                    </p>
                    <p>
                        <input type="submit" name="sendEmails"
                               value='<fmt:message key="journeysharer.submit"/>'/>
                        <input type="submit" name="cancelButton"
                               value='Cancel'/>
                    </p>
                </div>
            </form:form>

        </div>
        </div>



    <div class="yui-u"> </div>

                    </div>
                </div>
            </div>

<%-- LEFT NAV --%>
            <div class="yui-b">		<!--googleoff: all-->
                <div id="snav">
                    <div class="snavtop"></div>
                    <form method="get" action="http://search2.openobjects.com/kbroker/hea/economics/search.lsim" class="sform">
                        <fieldset>
                            <input type="text" name="qt" size="15" maxlength="1000" value="" class="sbox" style="width:100px" />
                            <input id="submit" type="submit" value="Search" class="gobutton" style="width:4em" />
                            <input type="hidden" name="sr" value="0" />
                            <input type="hidden" name="nh" value="10" />
                            <input type="hidden" name="cs" value="iso-8859-1" />
                            <input type="hidden" name="sc" value="hea" />
                            <input type="hidden" name="sm" value="0" />
                            <input type="hidden" name="mt" value="1" />
                            <input type="hidden" name="ha" value="1022" />
                        </fieldset>
                    </form>
                    <%-- Left nav links --%>
                    <div class="content">
                        <ul>
                            <%@ include file="includes/headerLinks.jsp" %>
                            <%@ include file="includes/headerBrowse.jsp" %>
                        </ul>
                    </div>
                </div>
                <div class="snavbtm"></div>
                <%@include file="includes/econNetLeftNavBottom.jsp" %>
                </div>
            </div>

 <%@ include file="includes/footer.jsp" %>

    </div>
    <script src="http://www.economicsnetwork.ac.uk/gatag.js" type="text/javascript"></script>
    <script type="text/javascript">var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));</script>
    <script type="text/javascript">try{var pageTracker = _gat._getTracker("UA-1171701-1");pageTracker._trackPageview();} catch(err) {}</script>



</body>
</html>