<%-- 
    Document   : CreateBloodDonationJ
    Created on : Apr 4, 2021, 5:31:34 PM
    Author     : Juan Ni
--%>

<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${title}"/></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" 
              rel="stylesheet" integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" 
              crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="style/inputJSPstyle.css">
    </head>
    <body>
        <div>
            <div class="inputJSPWrapper container">
                <form method="POST">
                    
                    <c:forEach items="${bloodDonationColumnCodes}" var="colCode" varStatus="status">
                        <div class="mb-3 row">
                            <label class="col-sm-4 col-form-label">${bloodDonationColumnNames[status.index]}</label>
                            <div class="col-sm-8">
                                <c:choose>
                                    <c:when test="${(colCode == 'bank_id')}">
                                        <select name="${colCode}" class="form-select">
                                            <c:forEach items="${bloodBankIDs}" var="bankID">
                                                <option value="${bankID}">${bankID}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${(colCode == 'blood_group')}">
                                        <select name="${colCode}" class="form-select">
                                            <c:forEach items="${bloodGroupList}" var="bloodGroupValue">
                                                <option value="${bloodGroupValue}">${bloodGroupValue}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${(colCode == 'rhesus_factor')}">
                                        <select name="${colCode}" class="form-select">
                                            <c:forEach items="${rhdList}" var="rhdValue">
                                                <option value="${rhdValue}">${rhdValue}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${(colCode == 'created')}">
                                       <input type="datetime-local" step="1" class="form-control" name="${colCode}">
                                    </c:when>
                                    <c:otherwise>
                                       <input type="text" class="form-control" name="${colCode}">
                                    </c:otherwise>

                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                    
                    <div class="submitAndViewRow">
                        <input type="submit" class="btn btn-primary" name="view" value="Add and View">
                        <input type="submit" class="btn btn-primary" name="add" value="Add">
                        <pre>${path}</pre>
                        <p class="errorMessage">${errorMessage}</p>
                        <pre>Submitted keys and values: ${request}</pre>
                    </div>

                </form>
            </div>
        </div>
        
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js" 
        integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf" 
        crossorigin="anonymous"></script>
    </body>
</html>
