function showSearch(){
    $("#searchForm").toggleClass("visible");
}

$(document).ready(function(){
    $("#search").click(function(){
        showSearch();
    })
})
