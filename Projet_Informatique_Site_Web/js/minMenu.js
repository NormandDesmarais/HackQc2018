function showMenu() {
    var x = document.getElementById("navBar");
    if (x.className === "navBar") {
        x.className += " responsive";
    } else {
        x.className = "navBar";
    }
}

function hideMenu() {
    var x = document.getElementById("navBar");
    if (x.className === "navBar responsive") {
        x.className = "navBar";
    } else {
        x.className = "navBar";
    }
}