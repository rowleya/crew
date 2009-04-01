<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>The password has changed.</title>
    <link href="./style.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNav.jsp" %>

    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">

        <p>Your password has changed.</p>

        <p>Return to the <a href="."> home page</a>.</p>

    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>