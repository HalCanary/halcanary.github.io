# Lookup Book by ISBN

<div id="target"></div>
<script>
// Example:
// https://halcanary.org/isbn/?034549752X/The+City+and+The+City
function atb(target, t) {
    if (target == null) {
        alert("error"); return;
    }
    p = document.createElement("p");
    p.appendChild(document.createTextNode(t));
    target.appendChild(p);
}
function appendStrongPar(target, t) {
    if (target == null) {
        alert("error"); return;
    }
    p = document.createElement("p");
    p.style.fontWeight = "bold";
    p.appendChild(document.createTextNode(t));
    target.appendChild(p);
}
function appendLinkPar(target, l, t) {
    if (target == null) {
        alert("error"); return;
    }
    p = document.createElement("p");
    a = document.createElement("a");
    a.href = l;
    a.appendChild(document.createTextNode(t));
    p.appendChild(a);
    target.appendChild(p);
}
function is_isbn10_valid(n) {
    var check = 0;
    for (var i = 0; i < 9; i++) {
        check += (10 - i) * ( + n.substring(i, i + 1));
    }
    var t = n.substring(9, 10);
    check += (t == "x" || t == "X") ? 10 : (+ t);
    return ((check % 11) == 0);
}
function get_isbn13_check(n) {
    var c = 0;
    for (var i = 0; i < 12; i += 2) {
        c += (+ n.substring(i, i + 1));
    }
    for (var i = 1; i < 12; i += 2) {
        c += 3 * (+ n.substring(i, i + 1));
    }
    return (10 - (c % 10)) % 10;
}
function is_isbn13_valid(n) {
    return get_isbn13_check(n) == (+ n.substring(12, 13));
}
function to_isbn13(n) {
    if (n.length != 10) { return n; }
    var isbn12 = "978" + n.substring(0, 9);
    return isbn12 + get_isbn13_check(isbn12 + "?");
}
function check_isbn_valid(n) {
    switch (n.length) {
        case 10:
            return is_isbn10_valid(n) ? null : "ERROR: ISBN 10 FAILS VALIDITY CHECK";
        case 13:
            return is_isbn13_valid(n) ? null : "ERROR: ISBN 13 FAILS VALIDITY CHECK";
        default:
            return "ERROR: ISBN LENGTH IS WRONG";
    }
}
window.addEventListener("load", () => {
    var target = document.getElementById("target"); // global
    var search =  window.location.search;
    var regex = /\?([0-9xX-]*)(\/(.*))?/;
    var match = regex.exec(search);
    if (match == null) {
        atb(target, "ERROR: MISSING ISBN");
        return;
    }
    isbn = match[1].replace("-","");
    var title = match[3];
    if (title) {
        title = decodeURIComponent((title+"").replace(/\+/g, "%20"));
        appendStrongPar(target, title);
    }
    var validity_status = check_isbn_valid(isbn);
    if (validity_status != null) {
        atb(target, validity_status);
        return;
    }
    var isbn13 = to_isbn13(isbn);
    appendLinkPar(target,
                  "https://www.barnesandnoble.com/w//?ean=" + isbn13,
                  "ISBN " + isbn + " @ barnesandnoble.com");
    appendLinkPar(target,
                  "https://smile.amazon.com/s?k=" + isbn13 + "&tag=hrmcb-20",
                  "ISBN " + isbn + " @ amazon.com");
    appendLinkPar(target,
                  "https://www.google.com/search?tbm=bks&q=isbn:"+ isbn,
                  "ISBN " + isbn + " @ books.google.com");
    if (title) {
        appendLinkPar(target,
                      "https://en.wikipedia.org/w/index.php?search=" + encodeURI(title),
                      "Wikipedia Search: \"" + title + "\"")
    }
});
</script>
