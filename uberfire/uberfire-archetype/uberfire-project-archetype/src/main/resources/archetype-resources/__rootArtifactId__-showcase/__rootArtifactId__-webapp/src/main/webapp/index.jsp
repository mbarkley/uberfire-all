#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

  <title>${capitalizedRootArtifactId} Showcase</title>
</head>
<body>
<iframe id="__gwt_historyFrame" style="width: 0; height: 0; border: 0"></iframe>
<%--<script type="text/javascript">--%>
  <%--var current_user = { name:"@{name}", roles:[@{roles}] }--%>
<%--</script>--%>
<!--add loading indicator while the app is being loaded-->
<div id="loading">
  <div class="loading-indicator">
    <img src="images/loading-icon.gif" width="32" height="32" style="margin-right: 8px; float: left; vertical-align: top;"/>
    Please wait<br/><span id="loading-msg">Loading application...</span>
  </div>
</div>

<!-- The GWT js file generated at run time -->
<script type="text/javascript" src='${package}.${capitalizedRootArtifactId}Showcase.nocache.js'></script>

</body>
</html>
