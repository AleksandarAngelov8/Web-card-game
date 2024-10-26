<!-- Erstellt von Angelov -->
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <title>Dashboard</title>
</head>
<body>
<h1>Welcome to the Dashboard! <#if loggedIn>${username}</#if></h1>
<#if loggedIn>
    <form action="/logout" method="post">
        <button>Logout</button>
    </form>
    <button onclick=window.location.href="http://localhost:4567/change_password">Change password:</button>

<#else>
    <button onclick=window.location.href="http://localhost:4567/login">Login</button>
</#if>
<button onclick=window.location.href="http://localhost:4567/info/speakers">Speakers</button>
<button onclick=window.location.href="http://localhost:4567/date_search_speeches/search">Speeches</button>
<button onclick=window.location.href="http://localhost:4567/full_text_search/search">Full-Text Search</button>
<button onclick=window.location.href="http://localhost:4567/date_search_cas/search">Diagrams for multiple speeches</button>
<button onclick=window.location.href="http://localhost:4567/protocols">Protocols (Export)</button>
<button <#if !manager>disabled class="disabledButton"</#if> onclick=window.location.href="http://localhost:4567/manage/protocols">Manage Protocols</button>
<button <#if !manager>disabled class="disabledButton"</#if> onclick=window.location.href="http://localhost:4567/manage/speakers">Manage Speakers</button>
<button <#if !manager>disabled class="disabledButton"</#if> onclick=window.location.href="http://localhost:4567/manage/speeches">Manage Speeches</button>
<button <#if !admin>disabled class="disabledButton"</#if> onclick=window.location.href="http://localhost:4567/admin/users">Manage Users</button>

</body>
</html>