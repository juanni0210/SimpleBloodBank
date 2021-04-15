<%-- 
    Document   : CreateRecords-BloodDonateForm
    Created on : Apr 6, 2021, 11:18:06 AM
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
        <script>

        </script>
    </head>
    <body>
        <div class="donateFormWrapper">
            <form method="POST">
                <div class="personTable container">
                    <h3>Person</h3>
                    <div class="row">
                        <c:forEach items="${personColumnCodes}" var="colCode" varStatus="status">
                            <div class="col-md-6">
                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">${personColumnNames[status.index]}</label>
                                    <div class="col-sm-9">
                                        <c:choose>
                                            <c:when test="${(colCode != 'birth')}">
                                               <input type="text" class="form-control" name="${colCode}">
                                            </c:when>
                                            <c:otherwise>
                                               <input type="datetime-local" step="1" class="form-control" name="${colCode}">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="bloodTable container">
                    <h3>Blood</h3>
                    <div class="row">
                        <c:forEach items="${bloodColumnCodes}" var="colCode" varStatus="status">
                            <div class="col-md-6">
                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">${bloodColumnNames[status.index]}</label>
                                    <div class="col-sm-9">
                                        <c:choose>
                                            <c:when test="${(colCode == 'milliliters')}">
                                                <input type="text" class="form-control" name="${colCode}">
                                            </c:when>
                                            <c:when test="${(colCode == 'rhesus_factor' or colCode == 'tested')}">
                                                <select name="${colCode}" class="form-select">
                                                    <c:forEach items="${positiveNegativeList}" var="pnValue">
                                                        <option value="${pnValue}">${pnValue}</option>
                                                    </c:forEach>
                                                </select>
                                            </c:when>
                                            <c:otherwise>
                                                <select name="${colCode}" class="form-select">
                                                    <c:forEach items="${bloodGroupList}" var="bloodGroupValue">
                                                        <option value="${bloodGroupValue}">${bloodGroupValue}</option>
                                                    </c:forEach>
                                                </select>
                                            </c:otherwise>

                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div> 
                    
                    
                </div>

                <div class="administrationTable container">
                    <h3>Administration</h3>
                     <div class="row">
                        <c:forEach items="${adminColumnCodes}" var="colCode" varStatus="status">
                            <div class="col-md-6">
                                <div class="mb-3 row">
                                    <label class="col-sm-3 col-form-label">${adminColumnNames[status.index]}</label>
                                    <div class="col-sm-9">
                                        <c:choose>
                                            <c:when test="${(colCode == 'created')}">
                                                <input type="datetime-local" step="1" class="form-control" name="${colCode}">
                                            </c:when>
                                            <c:when test="${(colCode == 'name')}">
                                                <select name="${colCode}" class="form-select">
                                                    <c:forEach items="${bloodBankNames}" var="bankName">
                                                        <option value="${bankName}">${bankName}</option>
                                                    </c:forEach>
                                                </select>
                                            </c:when>
                                            <c:otherwise>
                                               <input type="text" class="form-control" name="${colCode}">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
             
                    </div> 
                </div>
                
                <div class="container submitRow">
                    <div class="row">
                        <div class="col-md-11">
                            <p class="errorMessage">${errorMessage}</p>
                            <pre>Submitted keys and values: ${request}</pre>
                        </div>
                        <div class="col-md-1"><input class="btn btn-primary" type="submit" name="add" value="Add"></div>
                    </div>
                </div>
            </form>
        </div>
        
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js" 
        integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf" 
        crossorigin="anonymous"></script>
    </body>
</html>