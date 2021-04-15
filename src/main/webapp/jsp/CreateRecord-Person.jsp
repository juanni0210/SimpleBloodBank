<%-- 
    Document   : CreateRecord-Person
    Created on : Apr 12, 2021, 1:41:46 PM
    Author     : Sophie Sun
--%>

<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${title}"/></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" 
              rel="stylesheet" integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" 
              crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="style/inputJSPstyle.css">
    </head>
    <body>
        <div class="inputJSPWrapper container">
            <form method="post">
                <div class="mb-3 row">
                    <label class="col-sm-4 col-form-label">First Name</label><br>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="first_name">
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label class="col-sm-4 col-form-label">Last Name</label><br>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="last_name">
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label class="col-sm-4 col-form-label">Phone</label><br>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="phone">
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label class="col-sm-4 col-form-label">Address</label><br>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="address">
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label class="col-sm-4 col-form-label">Birth</label><br>
                    <div class="col-sm-8">
                        <input type="datetime-local" class="form-control" name="birth" step="1">
                    </div>
                </div>
                

                <div class="submitAndViewRow">
                    <input type="submit" class="btn btn-primary" name="view" value="Add and View">
                    <input type="submit" class="btn btn-primary" name="add" value="Add">
                    <pre>${path}</pre>
                    <p class="errorMessage">${error}</p>
                    <pre>Submitted keys and values: ${request}</pre>
                    
                </div>
            </form>

         </div>
            
         <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js" 
        integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf" 
        crossorigin="anonymous"></script>
    </body>
</html>